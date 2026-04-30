package com.roadto500.api.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "soldier")
public class Soldier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    // keeping this optional for now given non-focus on authentication, just a placeholder if I was to implement authentication later
    @Column
    private String password;

    // name specified because Spring/Hibernate converts camelCase Java fields to make snake_case columns by default
    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dob;

    // required because it affects scoring for non-combat MOS'
    @Column(nullable = false)
    private String gender;

    // required because it affects scoring
    @Column(nullable = false)
    private String mos;

    @Column(name = "created_at", nullable = false)
    private LocalDate createdAt;
}