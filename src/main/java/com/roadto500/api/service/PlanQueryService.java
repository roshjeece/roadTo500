package com.roadto500.api.service;

import com.roadto500.api.model.WeeklyPlan;
import com.roadto500.api.model.WeekStatus;
import com.roadto500.api.repository.WeeklyPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PlanQueryService {

    private final WeeklyPlanRepository weeklyPlanRepository;

    public Optional<WeeklyPlan> getActivePlan(Long soldierId) {
        List<WeeklyPlan> plans = weeklyPlanRepository
                .findBySoldier_IdAndWeekStatusOrderByIdDesc(soldierId, WeekStatus.ACTIVE);
        return plans.isEmpty() ? Optional.empty() : Optional.of(plans.getFirst());
    }
}