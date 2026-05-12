package com.roadto500.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "planned_exercise")
public class PlannedExercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "planned_session_id", nullable = false)
    @JsonIgnore
    private PlannedSession plannedSession;

    @ManyToOne
    @JoinColumn(name = "exercise_id", nullable = false)
    private Exercise exercise;

    @Column
    private Integer sets;

    @Column
    private Integer reps;

    @Column
    private Integer weight;

    @Column(name = "planned_exercise_unit")
    @Enumerated(EnumType.STRING)
    private PlannedExerciseUnit plannedExerciseUnit;

    @Column(name = "exercise_time")
    private Integer workTime;

    @Column(name = "exercise_distance")
    private Integer distance;

    @Column(name = "exercise_pace")
    private Integer pace;

}
