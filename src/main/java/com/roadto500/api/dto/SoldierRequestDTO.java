package com.roadto500.api.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class SoldierRequestDTO {
    private String name;
    private String password;
    private LocalDate dob;
    private String gender;
    private String mos;
}
