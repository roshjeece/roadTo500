package com.roadto500.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "exercise")
public class Exercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String categories;

    @JsonIgnore
    @OneToMany(mappedBy = "exercise", fetch = FetchType.LAZY)
    private List<ExerciseAftEvent> aftEventMappings;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ExerciseDifficulty difficulty;

    @Column(columnDefinition = "TEXT")
    private String description;

    // REPS, DURATION, DISTANCE, WEIGHT_BASED, CARDIO
    @Column(name = "prescription_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private PrescriptionType prescriptionType;


}
