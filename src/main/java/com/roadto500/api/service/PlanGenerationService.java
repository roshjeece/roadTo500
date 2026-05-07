package com.roadto500.api.service;

import com.roadto500.api.dto.GapAnalysisDTO;
import com.roadto500.api.model.*;
import com.roadto500.api.repository.*;
import com.roadto500.api.service.strategy.RuleBasedPrescriptionStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static com.roadto500.api.model.ExerciseDifficulty.*;
import static com.roadto500.api.model.WeekStatus.ACTIVE;

@Service
@RequiredArgsConstructor
public class PlanGenerationService {

    private final ExerciseRepository exerciseRepository;
    private final SoldierRepository soldierRepository;
    private final WeeklyPlanRepository weeklyPlanRepository;
    private final PlannedSessionRepository plannedSessionRepository;
    private final PlannedExerciseRepository plannedExerciseRepository;
    private final ExerciseAftEventRepository exerciseAftEventRepository;
    private final RuleBasedPrescriptionStrategy ruleBasedPrescriptionStrategy;
    private final PerformanceService performanceService;

    public WeeklyPlan generateWeeklyPlan(Long soldierId, boolean startToday) {
        Map<String, Integer> currentScores = performanceService.getCurrentScoresForSoldier(soldierId);
        GapAnalysisDTO gapAnalysisDTO = ruleBasedPrescriptionStrategy.analyze(currentScores);
        Soldier soldier = soldierRepository.findById(soldierId).orElseThrow();
        LocalDate weekStart;
        if (startToday) {
            weekStart = LocalDate.now();
        } else {
            weekStart = LocalDate.now();
            while (weekStart.getDayOfWeek() != DayOfWeek.MONDAY) {
                weekStart = weekStart.plusDays(1);
            }
        }
        LocalDate weekEnd = weekStart.plusDays(6);
        LocalDate currentDate = weekStart;

        WeeklyPlan weeklyPlan = new WeeklyPlan();
        weeklyPlan.setSoldier(soldier);
        weeklyPlan.setWeekStart(weekStart);
        weeklyPlan.setWeekEnd(weekEnd);
        weeklyPlan.setWeekStatus(ACTIVE);
        weeklyPlan.setGenerationDTG(LocalDateTime.now());
        weeklyPlanRepository.save(weeklyPlan);

        for (Map.Entry<SessionType, Integer> entry : gapAnalysisDTO.getSessionAllocation().entrySet()) {
            for (int i = 0; i < entry.getValue(); i++) {
                PlannedSession plannedSession = new PlannedSession();
                plannedSession.setWeeklyPlan(weeklyPlan);
                plannedSession.setSessionType(entry.getKey());
                plannedSession.setSessionDate(currentDate);
                plannedSession.setDayOfWeek(currentDate.getDayOfWeek());
                plannedSession.setDayStatus(DayStatus.ACTIVE);
                plannedSession.setDescription("placeholder description");
                plannedSessionRepository.save(plannedSession);
                currentDate = currentDate.plusDays(1);

                for (String eventAbbreviation : entry.getKey().getEvents()) {
                    ExerciseDifficulty difficulty = getDifficultyForScore(currentScores.get(eventAbbreviation));
                    List<Exercise> exercises = exerciseAftEventRepository
                            .findByAftEvent_Abbreviation(eventAbbreviation)
                            .stream()
                            .map(ExerciseAftEvent::getExercise)
                            .filter(e -> e.getDifficulty() == difficulty)
                            .limit(5)
                            .toList();

                    for (Exercise exercise : exercises) {
                        PlannedExercise plannedExercise = new PlannedExercise();
                        plannedExercise.setPlannedSession(plannedSession);
                        plannedExercise.setExercise(exercise);
                        plannedExerciseRepository.save(plannedExercise);
                    }


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
