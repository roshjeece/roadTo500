package com.roadto500.api.controller;

import com.roadto500.api.dto.AftScoreRequestDTO;
import com.roadto500.api.dto.AftScoreResponseDTO;
import com.roadto500.api.service.PerformanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/performance")
@RequiredArgsConstructor
public class PerformanceController {

    private final PerformanceService performanceService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AftScoreResponseDTO submitAftScores(@RequestBody AftScoreRequestDTO aftScoreRequestDTO) {
        return performanceService.submitAftScores(aftScoreRequestDTO);
    }

    @GetMapping("/scores/{id}")
    public Map<String, Integer> getCurrentScoresForSoldier(@PathVariable Long id) {
        return performanceService.getCurrentScoresForSoldier(id);
    }

    @PutMapping("/suggestions/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateImprovementSuggestion(@PathVariable Long id) {
        performanceService.updateImprovementSuggestions(id);
    }
}
