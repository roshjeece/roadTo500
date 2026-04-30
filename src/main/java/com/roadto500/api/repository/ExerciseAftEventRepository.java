package com.roadto500.api.repository;

import com.roadto500.api.model.ExerciseAftEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExerciseAftEventRepository extends JpaRepository<ExerciseAftEvent, Long> {
    List<ExerciseAftEvent> findByAftEvent_Abbreviation(String abbreviation);
}
