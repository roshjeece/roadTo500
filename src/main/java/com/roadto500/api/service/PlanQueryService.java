package com.roadto500.api.service;

import com.roadto500.api.model.WeeklyPlan;
import com.roadto500.api.model.WeekStatus;
import com.roadto500.api.repository.WeeklyPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlanQueryService {

    private final WeeklyPlanRepository weeklyPlanRepository;

    public WeeklyPlan getActivePlan(Long soldierId) {
        return weeklyPlanRepository
                .findBySoldier_IdAndWeekStatus(soldierId, WeekStatus.ACTIVE)
                .orElseThrow(() -> new IllegalStateException("No active plan found for soldier " + soldierId));
    }
}