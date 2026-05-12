package com.roadto500.api.dto;

import lombok.Data;

import java.util.List;

@Data
public class SessionResultRequestDTO {
    private Long sessionId;
    private Integer userRPE; // nullable
    private List<Long> failedExerciseIds; // exercise IDs, not plannedExercise IDs
}