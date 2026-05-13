package com.roadto500.api.service;

import com.roadto500.api.model.*;
import com.roadto500.api.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlanSupersessionService {

    private final WeeklyPlanRepository weeklyPlanRepository;
    private final PlannedSessionRepository plannedSessionRepository;

    public void supersedePlan(Long soldierId) {
        List<WeeklyPlan> activePlans = weeklyPlanRepository
                .findBySoldier_IdAndWeekStatusOrderByIdDesc(soldierId, WeekStatus.ACTIVE);

        for (WeeklyPlan activePlan : activePlans) {
            List<PlannedSession> sessions = activePlan.getPlannedSessions();
            for (PlannedSession session : sessions) {
                if (session.getDayStatus() == DayStatus.ACTIVE) {
                    session.setDayStatus(DayStatus.INCOMPLETE);
                    plannedSessionRepository.save(session);
                }
            }
            activePlan.setWeekStatus(WeekStatus.SUPERSEDED);
            weeklyPlanRepository.save(activePlan);
            log.info("Superseded plan {} for soldier {}", activePlan.getId(), soldierId);
        }
    }
}