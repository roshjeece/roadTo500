package com.roadto500.api.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "event_score")
public class EventScore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "aft_test_result_id", nullable = false)
    private AftTestResult aftTestResult;

    @ManyToOne
    @JoinColumn(name = "aft_event_id", nullable = false)
    private AftEvent aftEvent;

    // optional for MVP, it would require me to implement hundreds of rows of conversion data
    @Column(name = "raw_value")
    private Integer rawValue;

    @Column(name = "points_earned", nullable = false)
    private Integer pointsEarned;

}
