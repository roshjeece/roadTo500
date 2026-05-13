package com.roadto500.api.service;

import com.roadto500.api.model.*;
import com.roadto500.api.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlanManagementService {

    private final WeeklyPlanRepository weeklyPlanRepository;
    private final PlanGenerationService planGenerationService;
    private final PlanSupersessionService planSupersessionService;

    @Scheduled(cron = "0 0 0 * * *")
    public void autoGeneratePlans() {
        LocalDate today = LocalDate.now();
        List<WeeklyPlan> expiredPlans = weeklyPlanRepository
                .findByWeekStatusAndWeekEndBefore(WeekStatus.ACTIVE, today);

        if (expiredPlans.isEmpty()) {
            log.info("Scheduled plan generation: no expired plans found.");
            return;
        }

        for (WeeklyPlan expiredPlan : expiredPlans) {
            Long soldierId = expiredPlan.getSoldier().getId();
            try {
                log.info("Auto-generating plan for soldier {}", soldierId);
                planSupersessionService.supersedePlan(soldierId);
                planGenerationService.generateWeeklyPlan(soldierId, false);
            } catch (Exception e) {
                log.error("Failed to auto-generate plan for soldier {}: {}", soldierId, e.getMessage());
            }
        }
    }
}