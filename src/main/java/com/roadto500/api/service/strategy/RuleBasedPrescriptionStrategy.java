package com.roadto500.api.service.strategy;

import com.roadto500.api.dto.GapAnalysisDTO;
import com.roadto500.api.model.CheckInFrequency;
import com.roadto500.api.model.SessionType;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static com.roadto500.api.model.CheckInFrequency.*;

@Service
public class RuleBasedPrescriptionStrategy implements PrescriptionStrategy {
    @Override
    public GapAnalysisDTO analyze(Map<String, Integer> scores) {
        boolean isMaintenance = scores.values().stream().allMatch(score -> score == 100);

        if (isMaintenance) {
            return getGapAnalysisDTO();
        }

        GapAnalysisDTO gapAnalysisDTO = new GapAnalysisDTO();

        Map<String, Integer> gapHashMap = new HashMap<>();
        Map<String, CheckInFrequency> checkInFrequencyMap = new HashMap<>();
        Map<SessionType, Integer> sessionAllocationMap = new HashMap<>();
        int defaultTotalSessions = 7;

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
        double totalMaxGap = 0.0;
        Map<SessionType, Integer> maxGapPerSessionType = new HashMap<>();

        for (SessionType sessionType : SessionType.values()) {
            int maxGap = sessionType.getEvents()
                    .stream()
                    .mapToInt(gapHashMap::get)
                    .max()
                    .orElse(0);
            maxGapPerSessionType.put(sessionType, maxGap);
            totalMaxGap += maxGap;
        }

        for (SessionType sessionType : SessionType.values()) {
            int maxGap = maxGapPerSessionType.get(sessionType);
            int sessionsForEvent;
            if (maxGap == 0) {
                sessionsForEvent = sessionType.getMinSessions();
            } else {
                sessionsForEvent = Math.clamp(
                        Math.round((maxGap / totalMaxGap) * defaultTotalSessions),
                        sessionType.getMinSessions(),
                        sessionType.getMaxSessions());
            }
            sessionAllocationMap.put(sessionType, sessionsForEvent);
        }
        gapAnalysisDTO.setSessionAllocation(sessionAllocationMap);

        return gapAnalysisDTO;
    }

    private static @NonNull GapAnalysisDTO getGapAnalysisDTO() {
        GapAnalysisDTO gapAnalysisDTO = new GapAnalysisDTO();
        Map<String, Integer> gapPerEvent = Map.of(
                "MDL", 0, "HRP", 0, "SDC", 0, "PLK", 0, "2MR", 0
        );

        Map<SessionType, Integer> sessionAllocation = Map.of(
                SessionType.SESSION_1, 2,
                SessionType.SESSION_2, 2,
                SessionType.SESSION_3, 3
        );

        Map<String, CheckInFrequency> checkInFrequency = Map.of(
                "MDL", MONTHLY,
                "HRP", MONTHLY,
                "SDC", MONTHLY,
                "PLK", MONTHLY,
                "2MR", MONTHLY
        );
        gapAnalysisDTO.setGapPerEvent(gapPerEvent);
        gapAnalysisDTO.setSessionAllocation(sessionAllocation);
        gapAnalysisDTO.setCheckInFrequency(checkInFrequency);
        return gapAnalysisDTO;
    }

}