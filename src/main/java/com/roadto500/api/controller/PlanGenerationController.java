package com.roadto500.api.controller;

import com.roadto500.api.model.WeeklyPlan;
import com.roadto500.api.service.PlanGenerationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/plan")
@RequiredArgsConstructor
public class PlanGenerationController {

    private final PlanGenerationService planGenerationService;

    @PostMapping("/{soldierId}")
    @ResponseStatus(HttpStatus.CREATED)
    public WeeklyPlan createWeeklyPlan(@PathVariable Long soldierId, @RequestParam Boolean startToday) {
        return planGenerationService.generateWeeklyPlan(soldierId, startToday);
    }
}
