package com.project.watermelon.model;

import com.project.watermelon.util.Timestamped;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Entity
public class Ticket extends Timestamped {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long ticketId;

    @ManyToOne
    @JoinColumn(name = "concertMappingId", nullable = false)
    private ConcertMapping concertMapping;

    @OneToOne(mappedBy = "ticket")
    private Purchase purchase;

    @OneToOne
    @JoinColumn(name="seatId", nullable = false)
    private Seat seat;
}
