package com.roadto500.api.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class SoldierResponseDTO {
    private String name;
    private LocalDate dob;
    private String gender;
    private String mos;
}
