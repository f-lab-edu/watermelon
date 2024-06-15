package com.project.watermelon.repository;

import com.project.watermelon.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    boolean existsBySeat_SeatId(Long seatId);
}
