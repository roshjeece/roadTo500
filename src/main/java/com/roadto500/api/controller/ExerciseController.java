package com.roadto500.api.controller;

import com.roadto500.api.model.Exercise;
import com.roadto500.api.model.ExerciseDifficulty;
import com.roadto500.api.service.ExerciseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/exercises")
@RequiredArgsConstructor
public class ExerciseController {

    private final ExerciseService exerciseService;

    @GetMapping()
    public List<Exercise> getAllExercises() {
        return exerciseService.getAllExercises();
    }

    @GetMapping("/by_event")
    public List<Exercise> getExercisesByAftEvent(@RequestParam("event") String abbreviation) {
        return exerciseService.getExercisesByAftEvent(abbreviation);
    }

    @GetMapping("/by_difficulty")
    public List<Exercise> getExercisesByDifficulty(@RequestParam("difficulty") ExerciseDifficulty difficulty) {
        return exerciseService.getExercisesByDifficulty(difficulty);
    }
}
