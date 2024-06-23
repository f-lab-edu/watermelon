package com.project.watermelon.model;

import com.project.watermelon.enumeration.ReservationStatus;
import com.project.watermelon.util.Timestamped;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

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

//    @Column()
//    private Long ticketId;

    @Column(nullable = false)
    private Long reservationRank;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status = ReservationStatus.WAIT;

    @Column()
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime availableAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concertMappingId", nullable = false)
    private ConcertMapping concertMapping;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberId", nullable = false)
    private Member member;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticketId")
    private Ticket ticket;

    @Builder
    public Reservation(Long reservationRank, ReservationStatus status, ConcertMapping concertMapping, Member member) {
        this.reservationRank = reservationRank;
        this.status = status;
        this.concertMapping = concertMapping;
        this.member = member;
    }

    public void updateReservationStatusAvailable(LocalDateTime currentTimestamp) {
        this.status = ReservationStatus.AVAILABLE;
        this.availableAt = currentTimestamp;
    }
    public void updateReservationStatusExpire() {
        this.status = ReservationStatus.EXPIRED;
    }
}
