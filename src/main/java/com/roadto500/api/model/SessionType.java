package com.roadto500.api.model;

import lombok.Getter;

import java.util.List;

@Getter
public enum SessionType {
    SESSION_1(1,3,"PLK", "HRP"),
    SESSION_2(1,3,"MDL", "SDC"),
    SESSION_3(2,5,"2MR");

    private final int minSessions;
    private final int maxSessions;
    private final List<String> events;

    SessionType(int minSessions, int maxSessions, String... events) {
        this.minSessions = minSessions;
        this.maxSessions = maxSessions;
        this.events = List.of(events);
    }

}