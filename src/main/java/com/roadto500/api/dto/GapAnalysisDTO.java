package com.roadto500.api.dto;

import com.roadto500.api.model.CheckInFrequency;
import lombok.Data;

import java.util.Map;

@Data
public class GapAnalysisDTO {
    private Map<String, Integer> gapPerEvent;
    private Map<String, CheckInFrequency> checkInFrequency;
    private Map<String, Integer> sessionAllocation;

}

/*
* This is the engine's output after analyzing Soldier scores, answering these three questions:
* 1. How far is each event from 100?
* 2. How often should each event be checked in on?
* 3. How many sessions this week should focus on each event?
* */