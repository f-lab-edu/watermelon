package com.project.watermelon.repository;


import com.project.watermelon.enumeration.ReservationStatus;
import com.project.watermelon.model.Reservation;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    Optional<Reservation> findByMember_Email(String email);

    @EntityGraph(attributePaths = {"concertMapping", "member"})
    Optional<Reservation> findByConcertMapping_ConcertMappingIdAndMember_Email(@Param("concertMappingId") Long concertMappingId, @Param("email") String email);

    Optional<Reservation> findByMember_EmailAndReservationId(String email, Long reservationId);

    // @EntityGraph 를 통해 fetch join -> N+1 문제 방지
    @EntityGraph(attributePaths = {"concertMapping", "concertMapping.location"})
    List<Reservation> findDistinctByConcertMappingConcertDateAfter(@Param("currentTimestamp") LocalDateTime currentTimestamp);

    List<Reservation> findByConcertMappingConcertMappingIdAndStatusOrderByReservationRank(Long concertMappingId, ReservationStatus status, Pageable pageable);


    Long countByStatusInAndConcertMappingConcertMappingId(List<ReservationStatus> statuses, Long concertMappingId);


    @EntityGraph(attributePaths = {"concertMapping", "ticket", "ticket.seat"})
    List<Reservation> findByConcertMappingConcertMappingIdAndStatusIn(@Param("concertMappingId") Long concertMappingId, @Param("statuses") List<ReservationStatus> statuses);


    List<Reservation> findByStatusAndAvailableAtBeforeAndConcertMappingConcertMappingIdIn(
            ReservationStatus status, LocalDateTime expiryTime, List<Long> concertMappingIds, Pageable pageable);

    int countByConcertMapping_ConcertMappingIdAndStatusNotAndReservationRankLessThan(
            @Param("concertMappingId") Long concertMappingId,
            @Param("status") ReservationStatus status,
            @Param("reservationRank") Long reservationRank
    );

}

