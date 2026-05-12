package com.roadto500.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@Data
@Entity
@Table(name = "planned_session")
public class PlannedSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "weekly_plan_id", nullable = false)
    @JsonIgnore
    private WeeklyPlan weeklyPlan;

    @Column(name = "session_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private SessionType sessionType;

    @Column(name = "session_date", nullable = false)
    private LocalDate sessionDate;

    @Column(name = "day_of_week", nullable = false)
    @Enumerated(EnumType.STRING)
    private DayOfWeek dayOfWeek;

    @Column(name = "day_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private DayStatus dayStatus;

    @Column(name = "user_rpe")
    private Integer userRPE;

    // Key this is required. Soldiers deserve to understand why they're doing a specific session.
    @Column(name = "session_description", columnDefinition = "TEXT", nullable = false)
    private String description;

    @OneToMany(mappedBy = "plannedSession", fetch = FetchType.EAGER)
    private List<PlannedExercise> plannedExercises;

}
