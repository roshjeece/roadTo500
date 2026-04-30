package com.roadto500.api.service;

import com.roadto500.api.model.Exercise;
import com.roadto500.api.model.ExerciseAftEvent;
import com.roadto500.api.model.ExerciseDifficulty;
import com.roadto500.api.repository.ExerciseAftEventRepository;
import com.roadto500.api.repository.ExerciseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExerciseService {

    private final ExerciseRepository exerciseRepository;
    private final ExerciseAftEventRepository  exerciseAftEventRepository;

    public List<Exercise> getAllExercises() {
        return exerciseRepository.findAll();
    }

    public List<Exercise> getExercisesByAftEvent(String abbreviation) {
        return exerciseAftEventRepository.findByAftEvent_Abbreviation(abbreviation)
                .stream()
                .map(ExerciseAftEvent::getExercise)
                .collect(Collectors.toList());
    }

    public List<Exercise> getExercisesByDifficulty(ExerciseDifficulty exerciseDifficulty) {
        return exerciseRepository.findByDifficulty(exerciseDifficulty);
    }
}
