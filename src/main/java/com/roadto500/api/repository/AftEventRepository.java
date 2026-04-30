package com.roadto500.api.repository;

import com.roadto500.api.model.AftEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AftEventRepository extends JpaRepository<AftEvent, Long> {
    Optional<AftEvent> findByAbbreviation(String abbreviation);
}
