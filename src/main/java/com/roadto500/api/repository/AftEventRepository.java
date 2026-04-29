package com.roadto500.api.repository;

import com.roadto500.api.model.AftEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AftEventRepository extends JpaRepository<AftEvent, Long> {
}
