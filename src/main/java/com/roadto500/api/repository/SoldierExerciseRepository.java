package com.roadto500.api.repository;

import com.roadto500.api.model.SoldierExercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SoldierExerciseRepository extends JpaRepository<SoldierExercise, Long> {
    Optional<SoldierExercise> findBySoldier_IdAndExercise_Id(Long soldier_id, Long exercise_id);
}
