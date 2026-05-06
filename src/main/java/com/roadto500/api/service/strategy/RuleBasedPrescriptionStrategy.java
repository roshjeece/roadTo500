package com.roadto500.api.service.strategy;

import com.roadto500.api.dto.GapAnalysisDTO;
import com.roadto500.api.model.CheckInFrequency;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static com.roadto500.api.model.CheckInFrequency.*;

@Service
public class RuleBasedPrescriptionStrategy implements PrescriptionStrategy {
    @Override
    public GapAnalysisDTO analyze(Map<String, Integer> scores) {
        GapAnalysisDTO gapAnalysisDTO = new GapAnalysisDTO();

        Map<String, Integer> gapHashMap = new HashMap<>();
        Map<String, CheckInFrequency> checkInFrequencyMap = new HashMap<>();
        Map<String, Integer> sessionAllocationMap = new HashMap<>();
        int defaultTotalSessions = 5;
        double totalGap = 0.0;

        // gapPerEvent
        for (Map.Entry<String, Integer> entry : scores.entrySet()) {
            int gap = 100 - entry.getValue();
            gapHashMap.put(entry.getKey(), gap);
        }
        gapAnalysisDTO.setGapPerEvent(gapHashMap);

        // checkInFrequency
        for (Map.Entry<String, Integer> entry : gapHashMap.entrySet()) {
            if (entry.getValue() > 40) checkInFrequencyMap.put(entry.getKey(), EVERY_SESSION);
            else if (entry.getValue() > 20) checkInFrequencyMap.put(entry.getKey(), WEEKLY);
            else if (entry.getValue() > 0) checkInFrequencyMap.put(entry.getKey(), BIWEEKLY);
            else checkInFrequencyMap.put(entry.getKey(), MONTHLY);
        }
        gapAnalysisDTO.setCheckInFrequency(checkInFrequencyMap);

        // sessionAllocation
        for (int i : gapHashMap.values()) {
            totalGap += i;
        }
        for (Map.Entry<String, Integer> entry : gapHashMap.entrySet()) {
            int sessionsForEvent = 1;
            // if a Soldier has a 500 in each event, this case handling simply assigns one workout per event per week and ensures no divide by 0 error
            if (totalGap == 0) {
                sessionAllocationMap.put(entry.getKey(), sessionsForEvent);
            } else {
                sessionsForEvent = (int) Math.round((entry.getValue() / totalGap) * defaultTotalSessions);
                sessionAllocationMap.put(entry.getKey(), sessionsForEvent);
            }
        }
        gapAnalysisDTO.setSessionAllocation(sessionAllocationMap);

        return gapAnalysisDTO;
    }

}