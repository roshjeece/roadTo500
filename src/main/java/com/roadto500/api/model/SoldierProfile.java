package com.roadto500.api.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "soldier_profile")
public class SoldierProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "soldier_id", nullable = false, unique = true)
    private Soldier soldier;

    // Performance baselines
    @Column(name = "trap_bar_3rm", nullable = false)
    private Integer trapBar3RM;

    @Column(name = "last_hrp_count", nullable = false)
    private Integer lastHrpCount;

    @Column(name = "two_mile_time_seconds", nullable = false)
    private Integer twoMileTimeSeconds;

    @Column(name = "bench_press_1rm")
    private Integer benchPress1RM;

    // Physical profile
    @Column(name = "body_weight_lbs", nullable = false)
    private Integer bodyWeightLbs;

    @Column(name = "height_inches", nullable = false)
    private Integer heightInches;
}