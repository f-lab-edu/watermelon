package com.project.watermelon.model;

import com.project.watermelon.enumeration.ReservationStatus;
import com.project.watermelon.util.Timestamped;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Entity
public class Reservation extends Timestamped {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long reservationId;

    @Column(nullable = false)
    private Long rank;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status = ReservationStatus.WAIT;

    @ManyToOne
    @JoinColumn(name = "concertMappingId", nullable = false)
    private ConcertMapping concertMapping;

    @ManyToOne
    @JoinColumn(name = "memberId", nullable = false)
    private Member member;
}

