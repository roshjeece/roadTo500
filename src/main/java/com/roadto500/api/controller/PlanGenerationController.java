package com.roadto500.api.controller;

import com.roadto500.api.model.WeeklyPlan;
import com.roadto500.api.service.PlanGenerationService;
import com.roadto500.api.service.PlanQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/plan")
@RequiredArgsConstructor
public class PlanGenerationController {

    private final PlanGenerationService planGenerationService;
    private final PlanQueryService planQueryService;

    @PostMapping("/{soldierId}")
    @ResponseStatus(HttpStatus.CREATED)
    public WeeklyPlan createWeeklyPlan(@PathVariable Long soldierId, @RequestParam Boolean startToday) {
        return planGenerationService.generateWeeklyPlan(soldierId, startToday);
    }

    @GetMapping("/{soldierId}/active")
    public WeeklyPlan getActivePlan(@PathVariable Long soldierId) {
        return planQueryService.getActivePlan(soldierId);
    }
}