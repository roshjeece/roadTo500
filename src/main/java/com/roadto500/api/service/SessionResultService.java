package com.roadto500.api.service;

import com.roadto500.api.dto.SessionResultRequestDTO;
import com.roadto500.api.model.*;
import com.roadto500.api.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.roadto500.api.model.PrescriptionConstants.REP_PYRAMID;

@Service
@RequiredArgsConstructor
public class SessionResultService {

    private final PlannedSessionRepository plannedSessionRepository;
    private final SoldierExerciseRepository soldierExerciseRepository;
    private final SoldierRepository soldierRepository;
    private final SoldierProfileRepository soldierProfileRepository;

    public void logSessionResult(SessionResultRequestDTO dto) {
        PlannedSession session = plannedSessionRepository
                .findById(dto.getSessionId())
                .orElseThrow(() -> new IllegalArgumentException("Session not found: " + dto.getSessionId()));

        session.setDayStatus(DayStatus.COMPLETED);
        if (dto.getUserRPE() != null) {
            session.setUserRPE(dto.getUserRPE());
        }
        plannedSessionRepository.save(session);

        Long soldierId = session.getWeeklyPlan().getSoldier().getId();
        Soldier soldier = soldierRepository.findById(soldierId).orElseThrow();

        SoldierProfile profile = soldierProfileRepository
                .findBySoldier_Id(soldierId)
                .orElseThrow(() -> new IllegalStateException("SoldierProfile not found for soldier " + soldierId));

        Set<Long> failedExerciseIds = dto.getFailedExerciseIds() != null
                ? new HashSet<>(dto.getFailedExerciseIds())
                : Set.of();

        List<PlannedExercise> plannedExercises = session.getPlannedExercises();

        for (PlannedExercise plannedExercise : plannedExercises) {
            Exercise exercise = plannedExercise.getExercise();
            boolean failed = failedExerciseIds.contains(exercise.getId());

            SoldierExercise progression = soldierExerciseRepository
                    .findBySoldier_IdAndExercise_Id(soldierId, exercise.getId())
                    .orElseGet(() -> initProgression(soldier, exercise));

            progression.setLastPerformed(LocalDate.now());

            if (failed) {
                handleFailure(progression, exercise, profile);
            } else {
                handleSuccess(progression, exercise, profile, plannedExercise);
            }

            soldierExerciseRepository.save(progression);
        }
    }

    // --- INIT ---

    private SoldierExercise initProgression(Soldier soldier, Exercise exercise) {
        SoldierExercise progression = new SoldierExercise();
        progression.setSoldier(soldier);
        progression.setExercise(exercise);
        progression.setConsecutiveFailureCount(0);
        return progression;
    }

    // --- SUCCESS ---

    private void handleSuccess(SoldierExercise progression, Exercise exercise, SoldierProfile profile, PlannedExercise plannedExercise) {
        progression.setConsecutiveFailureCount(0);

        switch (exercise.getPrescriptionType()) {
            case WEIGHT_BASED -> advanceWeight(progression, exercise, plannedExercise);
            case REPS -> advanceReps(progression, exercise, profile);
            case CARDIO -> advanceCardio(progression, exercise, profile);
            case DURATION -> advanceDuration(progression, exercise);
            case DISTANCE -> {}
        }
    }

    // --- FAILURE ---

    private void handleFailure(SoldierExercise progression, Exercise exercise, SoldierProfile profile) {
        int newCount = progression.getConsecutiveFailureCount() + 1;
        if (newCount >= 3) {
            progression.setConsecutiveFailureCount(0);
            deload(progression, exercise, profile);
        } else {
            progression.setConsecutiveFailureCount(newCount);
        }
    }

    // --- ADVANCE ---

    private void advanceWeight(SoldierExercise progression, Exercise exercise, PlannedExercise plannedExercise) {
        if (exercise.getName().equals("Sled Drag")) return;
        int current = progression.getCurrentWeight() != null
                ? progression.getCurrentWeight()
                : plannedExercise.getWeight() != null ? plannedExercise.getWeight() : 0;
        progression.setCurrentWeight(current + 5);
    }

    private void advanceReps(SoldierExercise progression, Exercise exercise, SoldierProfile profile) {
        boolean isHrp = exercise.getName().equals("Hand-Release Push-Up");
        if (isHrp) {
            int base = (int) Math.round(profile.getLastHrpCount() * 0.55);
            int current = progression.getCurrentReps() != null ? progression.getCurrentReps() : base;
            progression.setCurrentReps(current + 2);
        } else {
            int stepIndex = progression.getPyramidStep() != null ? progression.getPyramidStep() : 0;
            progression.setPyramidStep(Math.min(stepIndex + 1, REP_PYRAMID.length - 1));
            progression.setCurrentReps(REP_PYRAMID[progression.getPyramidStep()][1]);
        }
    }

    private void advanceCardio(SoldierExercise progression, Exercise exercise, SoldierProfile profile) {
        switch (exercise.getName()) {
            case "800m Repeats" -> {
                int basePace = profile.getTwoMileTimeSeconds() / 4;
                int currentReps = progression.getCurrentReps() != null ? progression.getCurrentReps() : 4;
                int currentPace = progression.getCurrentDuration() != null ? progression.getCurrentDuration() : basePace;
                if (currentReps < 6) {
                    progression.setCurrentReps(currentReps + 1);
                } else {
                    progression.setCurrentReps(4);
                    progression.setCurrentDuration(currentPace - 10);
                }
            }
            case "Hill Run" -> {
                int currentReps = progression.getCurrentReps() != null ? progression.getCurrentReps() : 4;
                int currentDuration = progression.getCurrentDuration() != null ? progression.getCurrentDuration() : 60;
                if (currentReps < 8) {
                    progression.setCurrentReps(currentReps + 1);
                } else if (currentDuration < 90) {
                    progression.setCurrentReps(4);
                    progression.setCurrentDuration(90);
                }
                // At 8x90s — hold
            }
            case "Easy Conversational Run" -> {
                int current = progression.getCurrentDuration() != null ? progression.getCurrentDuration() : 20 * 60;
                progression.setCurrentDuration(current + 5 * 60);
            }
        }
    }

    private void advanceDuration(SoldierExercise progression, Exercise exercise) {
        switch (exercise.getName()) {
            case "Stair Climb" -> {
                int current = progression.getCurrentDuration() != null ? progression.getCurrentDuration() : 10 * 60;
                progression.setCurrentDuration(current + 2 * 60);
            }
            case "RKC Plank" -> {
                int current = progression.getCurrentDuration() != null ? progression.getCurrentDuration() : 20;
                if (current < 30) progression.setCurrentDuration(current + 5);
                // At 30s — hold
            }
            case "Long-Lever Plank", "Weighted Plank" -> {
                int current = progression.getCurrentDuration() != null ? progression.getCurrentDuration() : 30;
                if (current < 60) progression.setCurrentDuration(current + 5);
                // At 60s — hold
            }
            case "Standard Plank" -> {
                int current = progression.getCurrentDuration() != null ? progression.getCurrentDuration() : 60;
                progression.setCurrentDuration(current + 5);
            }
        }
    }

    // --- DELOAD ---

    private void deload(SoldierExercise progression, Exercise exercise, SoldierProfile profile) {
        switch (exercise.getPrescriptionType()) {
            case WEIGHT_BASED -> {
                if (exercise.getName().equals("Sled Drag")) return;
                int current = progression.getCurrentWeight() != null ? progression.getCurrentWeight() : 0;
                progression.setCurrentWeight((int) Math.round(current * 0.90));
            }
            case REPS -> {
                boolean isHrp = exercise.getName().equals("Hand-Release Push-Up");
                if (isHrp) {
                    int base = (int) Math.round(profile.getLastHrpCount() * 0.55);
                    int current = progression.getCurrentReps() != null ? progression.getCurrentReps() : base;
                    progression.setCurrentReps(Math.max(current - 2, base));
                } else {
                    int stepIndex = progression.getPyramidStep() != null ? progression.getPyramidStep() : 0;
                    progression.setPyramidStep(Math.max(stepIndex - 1, 0));
                    progression.setCurrentReps(REP_PYRAMID[progression.getPyramidStep()][1]);
                }
            }
            case CARDIO -> {
                switch (exercise.getName()) {
                    case "800m Repeats", "Hill Run" -> {
                        int current = progression.getCurrentReps() != null ? progression.getCurrentReps() : 4;
                        progression.setCurrentReps(Math.max(current - 1, 4));
                    }
                    case "Easy Conversational Run" -> {
                        int current = progression.getCurrentDuration() != null ? progression.getCurrentDuration() : 20 * 60;
                        progression.setCurrentDuration(Math.max(current - 5 * 60, 20 * 60));
                    }
                }
            }
            case DURATION -> {
                switch (exercise.getName()) {
                    case "Stair Climb" -> {
                        int current = progression.getCurrentDuration() != null ? progression.getCurrentDuration() : 10 * 60;
                        progression.setCurrentDuration(Math.max(current - 2 * 60, 10 * 60));
                    }
                    case "RKC Plank" -> {
                        int current = progression.getCurrentDuration() != null ? progression.getCurrentDuration() : 20;
                        progression.setCurrentDuration(Math.max(current - 5, 20));
                    }
                    case "Long-Lever Plank", "Weighted Plank" -> {
                        int current = progression.getCurrentDuration() != null ? progression.getCurrentDuration() : 30;
                        progression.setCurrentDuration(Math.max(current - 5, 30));
                    }
                    case "Standard Plank" -> {
                        int current = progression.getCurrentDuration() != null ? progression.getCurrentDuration() : 60;
                        progression.setCurrentDuration(Math.max(current - 5, 60));
                    }
                }
            }
            case DISTANCE -> {}
        }
    }
}