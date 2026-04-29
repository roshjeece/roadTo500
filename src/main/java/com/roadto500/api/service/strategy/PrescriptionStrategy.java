package com.roadto500.api.service.strategy;

import com.roadto500.api.dto.GapAnalysisDTO;
import com.roadto500.api.model.EventScore;

import java.util.List;

public interface PrescriptionStrategy {
    GapAnalysisDTO analyze(List<EventScore> scores);
}