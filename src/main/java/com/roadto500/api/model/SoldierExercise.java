package com.roadto500.api.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "soldier_exercise_progression")
public class SoldierExercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "soldier_id", nullable = false)
    private Soldier soldier;

    @ManyToOne
    @JoinColumn(name = "exercise_id", nullable = false)
    private Exercise exercise;

    @Column
    private Integer currentWeight;

    @Column
    private Integer currentReps;

    @Column
    private Integer currentDuration;

    @Column
    private Integer currentDistance;

    @Column
    private int consecutiveFailureCount = 0;

    @Column
    private LocalDate lastPerformed;
}
