package com.roadto500.api.model;

import jakarta.persistence.*;
import lombok.Data;

@Data // Lombok, auto-generates getters, setters, toString, equals, hashcode
@Entity // tells JPA this class maps to a database table
@Table(name = "aft_event") // table name
public class AftEvent {

    @Id // primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto increment
    private Long id;

    @Column(nullable = false, unique = true) // not null
    private String name;

    @Column(nullable = false)
    private String abbreviation;

    // be able to store multiple categories per event
    @Column(nullable = false)
    private String categories;

    // unit of display (lbs, reps, time)
    @Column(name = "unit", nullable = false)
    @Enumerated(EnumType.STRING)
    private AftEventUnit aftEventUnit;

    // Hibernate generates VARCHAR(255) when defining a private String, TEXT is necessary for longer description
    @Column(columnDefinition = "TEXT")
    private String description;
}