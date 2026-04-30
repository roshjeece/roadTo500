package com.roadto500.api.repository;

import com.roadto500.api.model.Exercise;
import com.roadto500.api.model.ExerciseDifficulty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExerciseRepository extends JpaRepository<Exercise, Long> {
    Optional<Exercise> findByName(String name);
    List<Exercise> findByDifficulty(ExerciseDifficulty difficulty);

}
