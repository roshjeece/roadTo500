package com.roadto500.api.service;

import com.roadto500.api.dto.SoldierRequestDTO;
import com.roadto500.api.dto.SoldierResponseDTO;
import com.roadto500.api.model.Soldier;
import com.roadto500.api.repository.AftTestResultRepository;
import com.roadto500.api.repository.EventScoreRepository;
import com.roadto500.api.repository.PlannedExerciseRepository;
import com.roadto500.api.repository.PlannedSessionRepository;
import com.roadto500.api.repository.SoldierProfileRepository;
import com.roadto500.api.repository.SoldierRepository;
import com.roadto500.api.repository.WeeklyPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SoldierService {

    private final SoldierRepository soldierRepository;

    public SoldierResponseDTO createSoldier(SoldierRequestDTO soldierRequestDTO) {
        Soldier soldier = new Soldier();
        soldier.setName(soldierRequestDTO.getName());
        soldier.setPassword(soldierRequestDTO.getPassword());
        soldier.setDob(soldierRequestDTO.getDob());
        soldier.setGender(soldierRequestDTO.getGender());
        soldier.setMos(soldierRequestDTO.getMos());
        soldier.setCreatedAt(LocalDate.now());
        soldierRepository.save(soldier);
        return newSoldierResponseDTO(soldier);
    }

    private SoldierResponseDTO newSoldierResponseDTO(Soldier soldier) {
        SoldierResponseDTO soldierResponseDTO = new SoldierResponseDTO();
        soldierResponseDTO.setId(soldier.getId());
        soldierResponseDTO.setName(soldier.getName());
        soldierResponseDTO.setDob(soldier.getDob());
        soldierResponseDTO.setGender(soldier.getGender());
        soldierResponseDTO.setMos(soldier.getMos());
        return soldierResponseDTO;
    }

    public SoldierResponseDTO getSoldierById(Long id) {
        return newSoldierResponseDTO(soldierRepository.findById(id).orElseThrow());
    }

    public void deleteSoldier(Long id) {
        soldierRepository.deleteById(id);
    }

    // full update/PUT operation, good enough for MVP
    public SoldierResponseDTO updateSoldier(Long id, SoldierRequestDTO soldierRequestDTO) {
        Soldier soldier = soldierRepository.findById(id).orElseThrow();
        soldier.setName(soldierRequestDTO.getName());
        soldier.setPassword(soldierRequestDTO.getPassword());
        soldier.setDob(soldierRequestDTO.getDob());
        soldier.setGender(soldierRequestDTO.getGender());
        soldier.setMos(soldierRequestDTO.getMos());
        return newSoldierResponseDTO(soldierRepository.save(soldier));
    }

    public List<SoldierResponseDTO> getAllSoldiers() {
        return soldierRepository.findAll()
                .stream()
                .map(this::newSoldierResponseDTO)
                .toList();
    }
}