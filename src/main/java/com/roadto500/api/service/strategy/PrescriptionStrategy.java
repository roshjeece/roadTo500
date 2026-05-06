package com.roadto500.api.service.strategy;

import com.roadto500.api.dto.GapAnalysisDTO;

import java.util.Map;

public interface PrescriptionStrategy {
    GapAnalysisDTO analyze(Map<String, Integer> scores);

}