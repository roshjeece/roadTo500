package com.roadto500.api.service;

import com.roadto500.api.model.*;
import com.roadto500.api.repository.SoldierExerciseRepository;
import com.roadto500.api.repository.SoldierProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.roadto500.api.model.PrescriptionConstants.REP_PYRAMID;

@Service
@RequiredArgsConstructor
public class ExercisePrescriptionService {

    private final SoldierProfileRepository soldierProfileRepository;
    private final SoldierExerciseRepository soldierExerciseRepository;

    private static final int[] KETTLEBELL_WEIGHTS = {35, 44, 53, 62, 70};

    public void prescribe(PlannedExercise plannedExercise, Long soldierId) {
        Exercise exercise = plannedExercise.getExercise();
        SoldierProfile profile = soldierProfileRepository
                .findBySoldier_Id(soldierId)
                .orElseThrow(() -> new IllegalStateException(
                        "SoldierProfile not found for soldier " + soldierId + ". Complete onboarding first."));

        SoldierExercise progression = soldierExerciseRepository
                .findBySoldier_IdAndExercise_Id(soldierId, exercise.getId())
                .orElse(null);

        switch (exercise.getPrescriptionType()) {
            case WEIGHT_BASED -> prescribeWeightBased(plannedExercise, exercise, profile, progression);
            case REPS -> prescribeRepBased(plannedExercise, exercise, profile, progression);
            case CARDIO -> prescribeCardio(plannedExercise, exercise, profile, progression);
            case DURATION -> prescribeDuration(plannedExercise, exercise, progression);
            case DISTANCE -> prescribeDistance(plannedExercise, exercise, profile);
        }
    }

    // --- WEIGHT BASED ---

    private void prescribeWeightBased(PlannedExercise pe, Exercise exercise, SoldierProfile profile, SoldierExercise progression) {
        if (exercise.getName().equals("Sled Drag")) {
            prescribeSledDrag(pe);
            return;
        }

        int startingWeight = resolveStartingWeight(exercise, profile);
        int currentWeight = progression != null && progression.getCurrentWeight() != null
                ? progression.getCurrentWeight()
                : startingWeight;

        pe.setSets(5);
        pe.setReps(5);
        pe.setWeight(currentWeight);
        pe.setPlannedExerciseUnit(PlannedExerciseUnit.LBS);
    }

    private int resolveStartingWeight(Exercise exercise, SoldierProfile profile) {
        return switch (exercise.getName()) {
            case "Trap Bar Deadlift" -> (int) Math.round(profile.getTrapBar3RM() * 0.85);
            case "Trap Bar Carry" -> (int) Math.round(profile.getTrapBar3RM() * 0.50);
            case "Romanian Deadlift" -> (int) Math.round(profile.getTrapBar3RM() * 0.70);
            case "Kettlebell Farmer Carry" -> {
                int raw = (int) Math.round(profile.getTrapBar3RM() * 0.50);
                yield roundToNearestKettlebell(raw);
            }
            default -> {
                int effectiveBench = profile.getBenchPress1RM() != null
                        ? profile.getBenchPress1RM()
                        : (int) Math.round(profile.getBodyWeightLbs() * 0.67);
                yield (int) Math.round(effectiveBench * 0.75);
            }
        };
    }

    private int roundToNearestKettlebell(int raw) {
        int nearest = KETTLEBELL_WEIGHTS[0];
        int minDiff = Math.abs(raw - nearest);
        for (int kb : KETTLEBELL_WEIGHTS) {
            int diff = Math.abs(raw - kb);
            if (diff < minDiff) {
                minDiff = diff;
                nearest = kb;
            }
        }
        return nearest;
    }

    private void prescribeSledDrag(PlannedExercise pe) {
        pe.setSets(5);
        pe.setDistance(25);
        pe.setWeight(90);
        pe.setPlannedExerciseUnit(PlannedExerciseUnit.METERS);
    }

    // --- REP BASED ---

    private void prescribeRepBased(PlannedExercise pe, Exercise exercise, SoldierProfile profile, SoldierExercise progression) {
        if (exercise.getName().equals("Hand-Release Push-Up")) {
            prescribeHrp(pe, profile, progression);
        } else {
            prescribeStandardRepBased(pe, progression);
        }
    }

    private void prescribeHrp(PlannedExercise pe, SoldierProfile profile, SoldierExercise progression) {
        int baseReps = (int) Math.round(profile.getLastHrpCount() * 0.55);
        int currentReps = progression != null && progression.getCurrentReps() != null
                ? progression.getCurrentReps()
                : baseReps;
        pe.setSets(5);
        pe.setReps(currentReps);
    }

    private void prescribeStandardRepBased(PlannedExercise pe, SoldierExercise progression) {
        int stepIndex = (progression != null && progression.getPyramidStep() != null)
                ? progression.getPyramidStep()
                : 0;
        stepIndex = Math.clamp(stepIndex, 0, REP_PYRAMID.length - 1);
        int[] step = REP_PYRAMID[stepIndex];
        pe.setSets(step[0]);
        pe.setReps(step[1]);
    }

    // --- CARDIO ---

    private void prescribeCardio(PlannedExercise pe, Exercise exercise, SoldierProfile profile, SoldierExercise progression) {
        switch (exercise.getName()) {
            case "800m Repeats" -> prescribe800mRepeats(pe, profile, progression);
            case "Easy Conversational Run" -> prescribeEasyRun(pe, progression);
            case "Hill Run" -> prescribeHillRun(pe, progression);
            default -> prescribeEasyRun(pe, progression);
        }
    }

    private void prescribe800mRepeats(PlannedExercise pe, SoldierProfile profile, SoldierExercise progression) {
        int basePaceSeconds = profile.getTwoMileTimeSeconds() / 4;
        int currentReps = progression != null && progression.getCurrentReps() != null
                ? progression.getCurrentReps() : 4;
        int currentPace = progression != null && progression.getCurrentDuration() != null
                ? progression.getCurrentDuration() : basePaceSeconds;
        pe.setSets(currentReps);
        pe.setDistance(800);
        pe.setPace(currentPace);
        pe.setPlannedExerciseUnit(PlannedExerciseUnit.METERS);
    }

    private void prescribeEasyRun(PlannedExercise pe, SoldierExercise progression) {
        int currentDuration = progression != null && progression.getCurrentDuration() != null
                ? progression.getCurrentDuration() : 20 * 60;
        pe.setWorkTime(currentDuration);
        pe.setPlannedExerciseUnit(PlannedExerciseUnit.SECONDS);
    }

    private void prescribeHillRun(PlannedExercise pe, SoldierExercise progression) {
        int currentReps = progression != null && progression.getCurrentReps() != null
                ? progression.getCurrentReps() : 4;
        int currentDuration = progression != null && progression.getCurrentDuration() != null
                ? progression.getCurrentDuration() : 60;
        pe.setSets(currentReps);
        pe.setWorkTime(currentDuration);
        pe.setPlannedExerciseUnit(PlannedExerciseUnit.SECONDS);
    }

    // --- DURATION ---

    private void prescribeDuration(PlannedExercise pe, Exercise exercise, SoldierExercise progression) {
        switch (exercise.getName()) {
            case "Standard Plank", "Weighted Plank" -> prescribePlank(pe, exercise);
            case "RKC Plank" -> prescribeRkcPlank(pe, progression);
            case "Long-Lever Plank" -> prescribeLongLeverPlank(pe, progression);
            default -> prescribeStairClimb(pe, progression);
        }
    }

    private void prescribePlank(PlannedExercise pe, Exercise exercise) {
        if (exercise.getName().equals("Weighted Plank")) {
            pe.setSets(3);
            pe.setWorkTime(30);
        } else {
            pe.setWorkTime(60);
        }
        pe.setPlannedExerciseUnit(PlannedExerciseUnit.SECONDS);
    }

    private void prescribeRkcPlank(PlannedExercise pe, SoldierExercise progression) {
        int currentDuration = progression != null && progression.getCurrentDuration() != null
                ? progression.getCurrentDuration() : 20;
        pe.setSets(3);
        pe.setWorkTime(Math.min(currentDuration, 30)); // ceiling 30s
        pe.setPlannedExerciseUnit(PlannedExerciseUnit.SECONDS);
    }

    private void prescribeLongLeverPlank(PlannedExercise pe, SoldierExercise progression) {
        int currentDuration = progression != null && progression.getCurrentDuration() != null
                ? progression.getCurrentDuration() : 30;
        pe.setSets(3);
        pe.setWorkTime(Math.min(currentDuration, 60)); // ceiling 60s
        pe.setPlannedExerciseUnit(PlannedExerciseUnit.SECONDS);
    }

    private void prescribeStairClimb(PlannedExercise pe, SoldierExercise progression) {
        int currentDuration = progression != null && progression.getCurrentDuration() != null
                ? progression.getCurrentDuration() : 10 * 60;
        pe.setWorkTime(currentDuration);
        pe.setPlannedExerciseUnit(PlannedExerciseUnit.SECONDS);
    }

    // --- DISTANCE ---

    private void prescribeDistance(PlannedExercise pe, Exercise exercise, SoldierProfile profile) {
        switch (exercise.getName()) {
            case "Sled Push" -> prescribeSledPush(pe, profile);
            case "Bear Crawl" -> prescribeBearCrawl(pe);
            default -> {
                pe.setDistance(25);
                pe.setPlannedExerciseUnit(PlannedExerciseUnit.METERS);
            }
        }
    }

    private void prescribeSledPush(PlannedExercise pe, SoldierProfile profile) {
        int weight = (int) Math.round(profile.getBodyWeightLbs() * 1.5);
        pe.setSets(5);
        pe.setDistance(25);
        pe.setWeight(weight);
        pe.setPlannedExerciseUnit(PlannedExerciseUnit.METERS);
    }

    private void prescribeBearCrawl(PlannedExercise pe) {
        pe.setSets(4);
        pe.setDistance(25);
        pe.setPlannedExerciseUnit(PlannedExerciseUnit.METERS);
    }
}