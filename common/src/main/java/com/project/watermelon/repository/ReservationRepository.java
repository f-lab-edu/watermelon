package com.project.watermelon.repository;


import com.project.watermelon.enumeration.ReservationStatus;
import com.project.watermelon.model.Reservation;
import com.project.watermelon.vo.ConcertMappingSeatInfoVO;
import com.project.watermelon.vo.ReservationSeatVo;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    Optional<Reservation> findByMember_Email(String email);

    Optional<Reservation> findByReservationId(Long reservationId);

    @Query("""
        SELECT DISTINCT new com.project.watermelon.vo.ConcertMappingSeatInfoVO(C.concertMappingId, L.seatCapacity)
        FROM Reservation R
        JOIN R.concertMapping C
        JOIN C.location L
        WHERE C.concertDate > :currentTimestamp
    """)
    List<ConcertMappingSeatInfoVO> retrieveConcertMappingSeatCapacities(@Param("currentTimestamp") LocalDateTime currentTimestamp);

    @Query("""
        SELECT r.reservationId
        FROM Reservation r
        WHERE r.concertMapping.concertMappingId = :concertMappingId
        AND r.status = 'WAIT'
        ORDER BY r.reservationRank
    """)
    List<Long> findReservationIdsForUpdate(@Param("concertMappingId") Long concertMappingId, Pageable pageable);

    @Query("""
        SELECT COUNT(*)
        FROM Reservation R
        WHERE R.status IN :statuses
        AND R.concertMapping.concertMappingId = :concertMappingId
    """)
    Long retrieveAvailableOrReservedCount(@Param("concertMappingId") Long concertMappingId, @Param("statuses") List<ReservationStatus> statuses);

    @Query("""
        SELECT new com.project.watermelon.vo.ReservationSeatVo(R.reservationId, T.ticketId, T.seat.seatId)
        FROM Reservation R
        JOIN Ticket T
        ON R.ticketId = T.ticketId
        WHERE R.concertMapping.concertMappingId = :concertMappingId
        AND R.status IN :statuses
    """)
    List<ReservationSeatVo> findByConcertMappingIdAndStatuses(@Param("concertMappingId") Long concertMappingId, @Param("statuses") List<ReservationStatus> statuses);

    @Modifying
    @Transactional
    @Query("""
        UPDATE Reservation r
        SET r.status = 'EXPIRED'
        WHERE r.status = 'AVAILABLE'
        AND r.availableAt < :expiryTime
        AND r.concertMapping.concertMappingId IN (
            SELECT cm.concertMappingId
            FROM ConcertMapping cm
            WHERE cm.concertDate > :currentTimestamp
        )
    """)
    void updateToExpiredStatus(@Param("currentTimestamp") LocalDateTime currentTimestamp, @Param("expiryTime") LocalDateTime expiryTime);

    @Modifying
    @Transactional
    @Query("""
        UPDATE Reservation r
        SET r.status = 'AVAILABLE', r.availableAt = :currentTimestamp
        WHERE r.reservationId IN :reservationIdList
    """)
    void updateReservationStatus(@Param("reservationIdList") List<Long> reservationIdList, @Param("currentTimestamp") LocalDateTime currentTimestamp);
}

