package com.project.watermelon.repository;


import com.project.watermelon.model.ConcertMapping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConcertMappingRepository extends JpaRepository<ConcertMapping, Long> {
    Optional<ConcertMapping> findConcertMappingByConcertMappingId(Long concertMappingId);
}
