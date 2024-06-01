package com.project.watermelon.repository;

import com.project.watermelon.model.Location;
import com.project.watermelon.model.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LocationRepository extends JpaRepository<Location, Long> {
    List<Location> findAllByLocationId(Long locationId);
}
