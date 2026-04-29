package com.roadto500.api.repository;

import com.roadto500.api.model.ExerciseAftEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExerciseAftEventRepository extends JpaRepository<ExerciseAftEvent, Long> {
}
