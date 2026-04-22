package com.roadto500.api.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "improvement_suggestion")
public class ImprovementSuggestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "soldier_id", nullable = false)
    private Soldier soldier;

    @ManyToOne
    @JoinColumn(name = "aft_event_id", nullable = false)
    private AftEvent aftEvent;

    @Column(name = "priority_level", nullable = false)
    @Enumerated(EnumType.STRING)
    private PriorityLevel priorityLevel;

    @Column(name = "event_score", nullable = false)
    private Integer score;

    @Column(name = "max_score_gap", nullable = false)
    private Integer gap;

    @Column(name = "date_suggestion_created", nullable = false)
    private LocalDate suggestionDate;

    @Column(name = "check_in_frequency", nullable = false)
    @Enumerated(EnumType.STRING)
    private CheckInFrequency checkInFrequency;

    @Column(name = "improvement_generation_source", nullable = false)
    @Enumerated(EnumType.STRING)
    private ImprovementGenerationSource source;

}