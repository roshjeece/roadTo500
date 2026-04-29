package com.roadto500.api.repository;

import com.roadto500.api.model.PlannedSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlannedSessionRepository extends JpaRepository<PlannedSession, Long> {
}
