package com.roadto500.api.service;

import com.roadto500.api.dto.GapAnalysisDTO;
import com.roadto500.api.model.*;
import com.roadto500.api.repository.*;
import com.roadto500.api.service.strategy.RecurringScheduleStrategy;
import com.roadto500.api.service.strategy.RuleBasedPrescriptionStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static com.roadto500.api.model.ExerciseDifficulty.*;
import static com.roadto500.api.model.WeekStatus.ACTIVE;

@Service
@RequiredArgsConstructor
public class PlanGenerationService {

    private final SoldierRepository soldierRepository;
    private final WeeklyPlanRepository weeklyPlanRepository;
    private final PlannedSessionRepository plannedSessionRepository;
    private final PlannedExerciseRepository plannedExerciseRepository;
    private final ExerciseAftEventRepository exerciseAftEventRepository;
    private final ExerciseRepository exerciseRepository;
    private final RuleBasedPrescriptionStrategy ruleBasedPrescriptionStrategy;
    private final RecurringScheduleStrategy recurringScheduleStrategy;
    private final PerformanceService performanceService;
    private final ExercisePrescriptionService exercisePrescriptionService;

    private static final Map<String, String> PRIMARY_EXERCISES = Map.of(
            "MDL", "Trap Bar Deadlift",
            "HRP", "Hand-Release Push-Up",
            "SDC", "Sled Drag",
            "PLK", "Standard Plank",
            "2MR", "800m Repeats"
    );

    public WeeklyPlan generateWeeklyPlan(Long soldierId, boolean startToday) {
        Map<String, Integer> currentScores = performanceService.getCurrentScoresForSoldier(soldierId);
        GapAnalysisDTO gapAnalysisDTO = ruleBasedPrescriptionStrategy.analyze(currentScores);
        Soldier soldier = soldierRepository.findById(soldierId).orElseThrow();
        boolean isMaintenance = currentScores.values().stream().allMatch(score -> score == 100);

        LocalDate weekStart = LocalDate.now();
        if (!startToday) {
            while (weekStart.getDayOfWeek() != DayOfWeek.MONDAY) {
                weekStart = weekStart.plusDays(1);
            }
        }
        LocalDate weekEnd = weekStart.plusDays(6);

        WeeklyPlan weeklyPlan = new WeeklyPlan();
        weeklyPlan.setSoldier(soldier);
        weeklyPlan.setWeekStart(weekStart);
        weeklyPlan.setWeekEnd(weekEnd);
        weeklyPlan.setWeekStatus(ACTIVE);
        weeklyPlan.setGenerationDTG(LocalDateTime.now());
        weeklyPlanRepository.save(weeklyPlan);

        Map<LocalDate, List<SessionType>> scheduledSessions = new TreeMap<>(
                recurringScheduleStrategy.schedule(gapAnalysisDTO.getSessionAllocation(), weekStart)
        );
        int session3count = 0;

        for (Map.Entry<LocalDate, List<SessionType>> dayEntry : scheduledSessions.entrySet()) {
            LocalDate sessionDate = dayEntry.getKey();
            for (SessionType sessionType : dayEntry.getValue()) {
                Set<Long> usedExerciseIds = new HashSet<>();

                String session3primary = "800m Repeats";
                if (sessionType == SessionType.SESSION_3) {
                    session3count++;
                    session3primary = session3count == 1 ? "800m Repeats" : "Easy Conversational Run";
                }

                PlannedSession plannedSession = new PlannedSession();
                plannedSession.setWeeklyPlan(weeklyPlan);
                plannedSession.setSessionType(sessionType);
                plannedSession.setSessionDate(sessionDate);
                plannedSession.setDayOfWeek(sessionDate.getDayOfWeek());
                plannedSession.setDayStatus(DayStatus.ACTIVE);
                plannedSession.setDescription("placeholder description");
                plannedSessionRepository.save(plannedSession);

                int supplementaryLimit = sessionType == SessionType.SESSION_3 ? 0 : 4;

                for (String eventAbbreviation : sessionType.getEvents()) {
                    ExerciseDifficulty difficulty = isMaintenance
                            ? ADVANCED
                            : getDifficultyForScore(currentScores.get(eventAbbreviation));
                    final String primaryName = eventAbbreviation.equals("2MR")
                            ? session3primary
                            : PRIMARY_EXERCISES.get(eventAbbreviation);

                    exerciseRepository.findByName(primaryName).ifPresent(primary -> {
                        if (usedExerciseIds.add(primary.getId())) {
                            PlannedExercise plannedExercise = new PlannedExercise();
                            plannedExercise.setPlannedSession(plannedSession);
                            plannedExercise.setExercise(primary);
                            exercisePrescriptionService.prescribe(plannedExercise, soldierId);
                            plannedExerciseRepository.save(plannedExercise);
                        }
                    });

                    List<Exercise> supplementary = exerciseAftEventRepository
                            .findByAftEvent_Abbreviation(eventAbbreviation)
                            .stream()
                            .map(ExerciseAftEvent::getExercise)
                            .filter(e -> sessionType != SessionType.SESSION_3
                                    ? e.getDifficulty() == difficulty
                                    : e.getDifficulty() != ADVANCED)
                            .filter(e -> !e.getName().equals(primaryName))
                            .collect(java.util.stream.Collectors.toCollection(ArrayList::new));

                    Collections.shuffle(supplementary);

                    supplementary.stream()
                            .filter(e -> usedExerciseIds.add(e.getId()))
                            .limit(supplementaryLimit)
                            .forEach(exercise -> {
                                PlannedExercise plannedExercise = new PlannedExercise();
                                plannedExercise.setPlannedSession(plannedSession);
                                plannedExercise.setExercise(exercise);
                                exercisePrescriptionService.prescribe(plannedExercise, soldierId);
                                plannedExerciseRepository.save(plannedExercise);
                            });
                }
            }
        }

        return weeklyPlanRepository.findById(weeklyPlan.getId()).orElseThrow();
    }

    private ExerciseDifficulty getDifficultyForScore(int score) {
        if (score > 79) return ADVANCED;
        if (score > 59) return INTERMEDIATE;
        else return BEGINNER;
    }
}