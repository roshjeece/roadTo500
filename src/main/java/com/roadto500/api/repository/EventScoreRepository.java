package com.roadto500.api.repository;

import com.roadto500.api.model.EventScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EventScoreRepository extends JpaRepository<EventScore, Long> {
    Optional<EventScore> findTopByAftTestResult_Soldier_IdAndAftEvent_AbbreviationOrderByAftTestResult_TestDateDesc(Long soldierId, String abbreviation);
}
