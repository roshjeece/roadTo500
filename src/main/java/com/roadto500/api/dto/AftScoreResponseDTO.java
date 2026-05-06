package com.roadto500.api.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.Map;

@Data
public class AftScoreResponseDTO {
    private String name;
    private Map<String, Integer> eventScore;
    private LocalDate dateOfTest;

}
