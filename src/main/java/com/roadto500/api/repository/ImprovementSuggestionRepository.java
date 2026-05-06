package com.roadto500.api.repository;

import com.roadto500.api.model.ImprovementSuggestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ImprovementSuggestionRepository extends JpaRepository<ImprovementSuggestion, Long> {
    Optional<ImprovementSuggestion> findBySoldier_IdAndAftEvent_Abbreviation(Long soldierId, String abbreviation);
    List<ImprovementSuggestion>findBySoldier_Id(Long soldierID);
}
