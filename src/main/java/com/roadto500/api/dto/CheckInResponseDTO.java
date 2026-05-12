package com.roadto500.api.dto;

import lombok.Data;

import java.util.List;

@Data
public class CheckInResponseDTO {
    private boolean checkInRequired;
    private List<String> dueEvents; // abbreviations of events with due check-ins
}