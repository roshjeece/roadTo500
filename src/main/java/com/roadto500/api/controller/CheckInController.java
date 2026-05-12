package com.roadto500.api.controller;

import com.roadto500.api.dto.CheckInRequestDTO;
import com.roadto500.api.dto.CheckInResponseDTO;
import com.roadto500.api.service.CheckInService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/sessions/{sessionId}/checkins")
@RequiredArgsConstructor
public class CheckInController {

    private final CheckInService checkInService;

    @GetMapping
    public CheckInResponseDTO getCheckInStatus(@PathVariable Long sessionId) {
        return checkInService.getCheckInStatus(sessionId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void submitCheckIn(@PathVariable Long sessionId,
                              @RequestBody CheckInRequestDTO dto) {
        checkInService.submitCheckIn(sessionId, dto);
    }
}