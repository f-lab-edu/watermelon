package com.project.watermelon.repository;

import com.project.watermelon.model.Concert;
import com.project.watermelon.model.Seat;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SeatRepository extends JpaRepository<Seat, Long> {
    @EntityGraph(attributePaths = {"location"})
    List<Seat> findAllByLocation_LocationId(Long locationId);

    Optional<Seat> findBySeatId(Long seatId);
}
