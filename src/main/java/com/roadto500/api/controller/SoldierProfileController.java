package com.roadto500.api.controller;

import com.roadto500.api.dto.SoldierProfileRequestDTO;
import com.roadto500.api.dto.SoldierProfileResponseDTO;
import com.roadto500.api.service.SoldierProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/soldiers/{soldierId}/profile")
@RequiredArgsConstructor
public class SoldierProfileController {

    private final SoldierProfileService soldierProfileService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SoldierProfileResponseDTO submitSoldierProfile(
            @PathVariable Long soldierId,
            @RequestBody SoldierProfileRequestDTO dto) {
        return soldierProfileService.submitSoldierProfile(soldierId, dto);
    }

    @GetMapping
    public SoldierProfileResponseDTO getSoldierProfile(@PathVariable Long soldierId) {
        return soldierProfileService.getSoldierProfile(soldierId);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public SoldierProfileResponseDTO updateSoldierProfile(
            @PathVariable Long soldierId,
            @RequestBody SoldierProfileRequestDTO dto) {
        return soldierProfileService.submitSoldierProfile(soldierId, dto);
    }
}