package com.project.watermelon.repository;


import com.project.watermelon.model.ConcertMapping;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ConcertMappingRepository extends JpaRepository<ConcertMapping, Long> {
    Optional<ConcertMapping> findConcertMappingByConcertMappingId(Long concertMappingId);

    @EntityGraph(attributePaths = {"concert", "location"})
    List<ConcertMapping> findByConcert_ConcertId(Long concertId);


    List<ConcertMapping> findByConcertDateAfter(LocalDateTime currentTimestamp);

}
