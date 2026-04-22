package com.roadto500.api.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "exercise_aft_event")
public class ExerciseAftEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "exercise_id", nullable = false)
    private Exercise exercise;

    @ManyToOne
    @JoinColumn(name = "aft_event_id", nullable = false)
    private AftEvent aftEvent;

    @Column(name = "contribution_level", nullable = false)
    @Enumerated(EnumType.STRING)
    private ContributionLevel contributionLevel;


}
