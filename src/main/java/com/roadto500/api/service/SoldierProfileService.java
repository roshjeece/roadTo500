package com.roadto500.api.service;

import com.roadto500.api.dto.SoldierProfileRequestDTO;
import com.roadto500.api.dto.SoldierProfileResponseDTO;
import com.roadto500.api.model.Soldier;
import com.roadto500.api.model.SoldierProfile;
import com.roadto500.api.repository.SoldierProfileRepository;
import com.roadto500.api.repository.SoldierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SoldierProfileService {

    private final SoldierRepository soldierRepository;
    private final SoldierProfileRepository soldierProfileRepository;

    public SoldierProfileResponseDTO submitSoldierProfile(Long soldierId, SoldierProfileRequestDTO dto) {
        Soldier soldier = soldierRepository.findById(soldierId).orElseThrow();

        SoldierProfile profile = soldierProfileRepository
                .findBySoldier_Id(soldierId)
                .orElse(new SoldierProfile());

        profile.setSoldier(soldier);
        profile.setTrapBar3RM(dto.getTrapBar3RM());
        profile.setLastHrpCount(dto.getLastHrpCount());
        profile.setTwoMileTimeSeconds(dto.getTwoMileTimeSeconds());
        profile.setBenchPress1RM(dto.getBenchPress1RM());
        profile.setBodyWeightLbs(dto.getBodyWeightLbs());
        profile.setHeightInches(dto.getHeightInches());

        soldierProfileRepository.save(profile);
        return buildResponse(soldier, profile);
    }

    public SoldierProfileResponseDTO getSoldierProfile(Long soldierId) {
        Soldier soldier = soldierRepository.findById(soldierId).orElseThrow();
        SoldierProfile profile = soldierProfileRepository
                .findBySoldier_Id(soldierId)
                .orElseThrow();
        return buildResponse(soldier, profile);
    }

    private SoldierProfileResponseDTO buildResponse(Soldier soldier, SoldierProfile profile) {
        SoldierProfileResponseDTO response = new SoldierProfileResponseDTO();
        response.setSoldierId(soldier.getId());
        response.setSoldierName(soldier.getName());
        response.setTrapBar3RM(profile.getTrapBar3RM());
        response.setLastHrpCount(profile.getLastHrpCount());
        response.setTwoMileTimeSeconds(profile.getTwoMileTimeSeconds());
        response.setBenchPress1RM(profile.getBenchPress1RM());
        response.setBodyWeightLbs(profile.getBodyWeightLbs());
        response.setHeightInches(profile.getHeightInches());

        if (profile.getBenchPress1RM() == null) {
            int estimated = (int) Math.round(profile.getBodyWeightLbs() * 0.67);
            response.setBenchEstimated(true);
            response.setEstimatedBench1RM(estimated);
        } else {
            response.setBenchEstimated(false);
            response.setEstimatedBench1RM(null);
        }

        return response;
    }

}