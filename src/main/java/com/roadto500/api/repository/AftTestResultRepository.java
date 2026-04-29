package com.roadto500.api.repository;

import com.roadto500.api.model.AftTestResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AftTestResultRepository extends JpaRepository<AftTestResult, Long> {
}
