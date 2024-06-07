package com.project.watermelon.model;

import com.project.watermelon.enumeration.MemberRole;
import com.project.watermelon.enumeration.ReservationStatus;
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
@Table(name = "RESERVATION", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"concertMappingId", "memberId"})
})
public class Reservation extends Timestamped {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long reservationId;

    @Column(nullable = false)
    private Long reservationRank;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status = ReservationStatus.WAIT;

    @ManyToOne
    @JoinColumn(name = "concertMappingId", nullable = false)
    private ConcertMapping concertMapping;

    @ManyToOne
    @JoinColumn(name = "memberId", nullable = false)
    private Member member;

    @Builder
    public Reservation(Long reservationRank, ReservationStatus status, ConcertMapping concertMapping, Member member) {
        this.reservationRank = reservationRank;
        this.status = status;
        this.concertMapping = concertMapping;
        this.member = member;
    }
}
