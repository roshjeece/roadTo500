package com.roadto500.api.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.DayOfWeek;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "planned_session")
public class PlannedSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "weekly_plan_id", nullable = false)
    private WeeklyPlan weeklyPlan;

    @ManyToOne
    @JoinColumn(name = "check_in_set")
    private AftEvent aftEvent;

    @Column(name = "session_date", nullable = false)
    private LocalDate sessionDate;

    @Column(name = "day_of_week", nullable = false)
    @Enumerated(EnumType.STRING)
    private DayOfWeek dayOfWeek;

    @Column(name = "day_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private DayStatus dayStatus;

    @Column(name = "planned_min_rpe")
    private Integer plannedMinRPE;

    @Column(name = "planned_max_rpe")
    private Integer plannedMaxRPE;

    @Column(name = "user_rpe")
    private Integer userRPE;

    // Key this is required. Soldiers deserve to understand why they're doing a specific session.
    @Column(name = "session_description", columnDefinition = "TEXT", nullable = false)
    private String description;

}
