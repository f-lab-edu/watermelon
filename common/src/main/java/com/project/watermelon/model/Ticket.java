package com.project.watermelon.model;

import com.project.watermelon.util.Timestamped;
import jakarta.persistence.*;
import lombok.Builder;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concertMappingId", nullable = false)
    private ConcertMapping concertMapping;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="seatId", nullable = false)
    private Seat seat;

    @Builder
    public Ticket(ConcertMapping concertMapping, Seat seat) {
        this.concertMapping = concertMapping;
        this.seat = seat;
    }
}
