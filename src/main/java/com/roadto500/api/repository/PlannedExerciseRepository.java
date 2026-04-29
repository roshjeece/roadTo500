package com.roadto500.api.repository;

import com.roadto500.api.model.PlannedExercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlannedExerciseRepository extends JpaRepository<PlannedExercise, Long> {
}
