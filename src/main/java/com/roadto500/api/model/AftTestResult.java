package com.roadto500.api.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "aft_test_result")
public class AftTestResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne // many AFT test results to one soldier ID
    @JoinColumn(name = "soldier_id", nullable = false)
    private Soldier soldier;

    @Column(name = "test_date", nullable = false)
    private LocalDate testDate;

    @Column(name = "total_score", nullable = false)
    private Integer totalScore;

    // optional
    @Column(columnDefinition = "TEXT")
    private String notes;
}
