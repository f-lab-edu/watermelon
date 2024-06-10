package com.project.watermelon.repository;

import com.project.watermelon.model.Concert;
import com.project.watermelon.model.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Long> {
    List<Seat> findAllByLocation_LocationId(Long locationId);
}
