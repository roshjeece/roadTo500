package com.roadto500.api.service.strategy;

import com.roadto500.api.model.SessionType;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface ScheduleStrategy {
    Map<LocalDate, List<SessionType>> schedule(Map<SessionType, Integer> unscheduled, LocalDate weekStart);
}
