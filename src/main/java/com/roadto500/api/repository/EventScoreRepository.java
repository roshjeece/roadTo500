package com.roadto500.api.repository;

import com.roadto500.api.model.EventScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface EventScoreRepository extends JpaRepository<EventScore, Long> {

    @Query("SELECT es FROM EventScore es " +
            "WHERE es.aftTestResult.soldier.id = :soldierId " +
            "AND es.aftEvent.abbreviation = :abbreviation " +
            "ORDER BY es.aftTestResult.testDate DESC, es.aftTestResult.id DESC " +
            "LIMIT 1")
    Optional<EventScore> findMostRecentScoreBySoldierAndEvent(
            @Param("soldierId") Long soldierId,
            @Param("abbreviation") String abbreviation);

    @Query("SELECT MAX(es.aftTestResult.testDate) FROM EventScore es " +
            "WHERE es.aftTestResult.soldier.id = :soldierId " +
            "AND es.aftEvent.abbreviation = :abbreviation")
    Optional<LocalDate> findMostRecentScoreDateBySoldierAndEvent(
            @Param("soldierId") Long soldierId,
            @Param("abbreviation") String abbreviation);
}