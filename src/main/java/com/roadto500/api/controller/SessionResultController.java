package com.roadto500.api.controller;

import com.roadto500.api.dto.SessionResultRequestDTO;
import com.roadto500.api.service.SessionResultService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/sessions")
@RequiredArgsConstructor
public class SessionResultController {

    private final SessionResultService sessionResultService;

    @PostMapping("/result")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logSessionResult(@RequestBody SessionResultRequestDTO dto) {
        sessionResultService.logSessionResult(dto);
    }
}