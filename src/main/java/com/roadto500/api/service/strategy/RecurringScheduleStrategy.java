package com.roadto500.api.service.strategy;

import com.roadto500.api.model.SessionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.roadto500.api.model.SessionType.*;

@Service
@RequiredArgsConstructor
public class RecurringScheduleStrategy implements ScheduleStrategy {

    @Override
    public Map<LocalDate, List<SessionType>> schedule(Map<SessionType, Integer> unscheduled, LocalDate weekStart) {
        Map<LocalDate, List<SessionType>> schedule = new HashMap<>();

        sessionSchedule(SESSION_1, 0, schedule, unscheduled, weekStart);
        sessionSchedule(SESSION_2, 1, schedule, unscheduled, weekStart);
        sessionSchedule(SESSION_3, 0, schedule, unscheduled, weekStart);


        return schedule;
    }

    private void sessionSchedule(SessionType sessionType, int offset, Map<LocalDate, List<SessionType>> sessionMap, Map<SessionType, Integer> unscheduled, LocalDate weekstart) {
        int sessionCount = unscheduled.get(sessionType);
        int sessionGap = sessionCount > 1 ? (6 - offset) / (sessionCount - 1) : 0;
        LocalDate weekEnd = weekstart.plusDays(6);

        for (int i = 0; i < sessionCount; i++) {
            LocalDate date = weekstart.plusDays(offset + ((long) sessionGap * i));
            if (sessionType != SESSION_3) {
                // Try one day earlier first
                LocalDate earlier = date.minusDays(1);
                if (!earlier.isBefore(weekstart) && isValidPlacement(sessionType, earlier, sessionMap)) {
                    date = earlier;
                } else {
                    // Nudge forward
                    while (!isValidPlacement(sessionType, date, sessionMap)) {
                        date = date.plusDays(1);
                    }
                }
            }
            if (!date.isAfter(weekEnd)) {
                sessionMap.computeIfAbsent(date, k -> new ArrayList<>()).add(sessionType);
            }
        }
    }

    private boolean isValidPlacement(SessionType sessionType, LocalDate date, Map<LocalDate, List<SessionType>> sessionMap) {
        // No same type or opposing heavy type on same day
        if (sessionMap.containsKey(date) && sessionMap.get(date).stream().anyMatch(s ->
                s == sessionType ||
                        (sessionType == SESSION_1 && s == SESSION_2) ||
                        (sessionType == SESSION_2 && s == SESSION_1))) {
            return false;
        }
        // No same type on previous day
        if (sessionMap.containsKey(date.minusDays(1)) && sessionMap.get(date.minusDays(1)).stream().anyMatch(s -> s == sessionType)) {
            return false;
        }
        // No same type on next day
        return !sessionMap.containsKey(date.plusDays(1)) || sessionMap.get(date.plusDays(1)).stream().noneMatch(s -> s == sessionType);
    }
}
