package com.project.watermelon.repository;


import com.project.watermelon.model.ConcertMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ConcertMappingRepository extends JpaRepository<ConcertMapping, Long> {
    Optional<ConcertMapping> findConcertMappingByConcertMappingId(Long concertMappingId);

    @Query("SELECT m FROM ConcertMapping m JOIN m.concert c JOIN m.location l WHERE c.concertId = :concertId")
    List<ConcertMapping> findByConcertIdWithDetails(@Param("concertId") Long concertId);
}
