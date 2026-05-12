package com.roadto500.api.dto;

import lombok.Data;

import java.util.Map;

@Data
public class CheckInRequestDTO {
    private Map<String, Integer> eventScores; // abbreviation -> score
}