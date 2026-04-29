package com.roadto500.api.repository;

import com.roadto500.api.model.EventScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventScoreRepository extends JpaRepository<EventScore, Long> {
}
