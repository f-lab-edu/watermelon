package com.project.watermelon.repository;


import com.project.watermelon.model.ConcertMapping;
import com.project.watermelon.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    Optional<Reservation> findByMember_Email(String email);
}
