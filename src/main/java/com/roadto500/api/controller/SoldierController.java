package com.roadto500.api.controller;

import com.roadto500.api.dto.SoldierRequestDTO;
import com.roadto500.api.dto.SoldierResponseDTO;
import com.roadto500.api.service.SoldierService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/soldiers")
@RequiredArgsConstructor
public class SoldierController {

    private final SoldierService soldierService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SoldierResponseDTO createSoldier(@RequestBody SoldierRequestDTO soldierRequestDTO) {
        return soldierService.createSoldier(soldierRequestDTO);
    }

    @GetMapping("/{id}")
    public SoldierResponseDTO getSoldierById(@PathVariable Long id) {
        return soldierService.getSoldierById(id);
    }

    @PutMapping("/{id}")
    public SoldierResponseDTO updateSoldier(@PathVariable Long id, @RequestBody SoldierRequestDTO soldierRequestDTO) {
        return soldierService.updateSoldier(id, soldierRequestDTO);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSoldier(@PathVariable Long id) {
        soldierService.deleteSoldier(id);
    }
}
