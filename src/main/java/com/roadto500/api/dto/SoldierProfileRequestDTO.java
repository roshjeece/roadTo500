package com.roadto500.api.dto;

import lombok.Data;

@Data
public class SoldierProfileRequestDTO {
    private Integer trapBar3RM;
    private Integer lastHrpCount;
    private Integer twoMileTimeSeconds;
    private Integer benchPress1RM; // nullable
    private Integer bodyWeightLbs;
    private Integer heightInches;
}