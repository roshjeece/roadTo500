package com.roadto500.api.repository;

import com.roadto500.api.model.SoldierProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SoldierProfileRepository extends JpaRepository<SoldierProfile, Long> {
    Optional<SoldierProfile> findBySoldier_Id(Long soldierId);

}