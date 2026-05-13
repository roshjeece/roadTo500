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
    List<WeeklyPlan> findBySoldier_IdAndWeekStatusOrderByIdDesc(Long soldierId, WeekStatus weekStatus);
    List<WeeklyPlan> findByWeekStatusAndWeekEndBefore(WeekStatus weekStatus, LocalDate date);

}