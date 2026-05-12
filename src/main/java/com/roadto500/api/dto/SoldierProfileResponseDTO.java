package com.roadto500.api.dto;

import lombok.Data;

@Data
public class SoldierProfileResponseDTO {
    private Long soldierId;
    private String soldierName;
    private Integer trapBar3RM;
    private Integer lastHrpCount;
    private Integer twoMileTimeSeconds;
    private Integer benchPress1RM;
    private Integer bodyWeightLbs;
    private Integer heightInches;
    private boolean benchEstimated;
    private Integer estimatedBench1RM;
}