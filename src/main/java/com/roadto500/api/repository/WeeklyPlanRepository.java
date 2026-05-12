package com.roadto500.api.repository;

import com.roadto500.api.model.WeekStatus;
import com.roadto500.api.model.WeeklyPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface WeeklyPlanRepository extends JpaRepository<WeeklyPlan, Long> {
    Optional<WeeklyPlan> findBySoldier_IdAndWeekStatus(Long soldierId, WeekStatus weekStatus);
    List<WeeklyPlan> findByWeekStatusAndWeekEndLessThanEqual(WeekStatus weekStatus, LocalDate date);
}
