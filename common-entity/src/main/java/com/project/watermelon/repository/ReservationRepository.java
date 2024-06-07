package com.project.watermelon.repository;


import com.project.watermelon.enumeration.ReservationStatus;
import com.project.watermelon.model.Reservation;
import com.project.watermelon.vo.ConcertMappingSeatInfoVO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    Optional<Reservation> findByMember_Email(String email);

    @Query("""
        SELECT new com.project.watermelon.vo.ConcertMappingSeatInfoVO(c.concertMappingId, l.seatCapacity)
        FROM Reservation R
        JOIN R.concertMapping C
        JOIN C.location L
        WHERE C.concertDate > CURRENT_TIMESTAMP
    """)
    List<ConcertMappingSeatInfoVO> retrieveConcertMappingSeatCapacities();

    @Transactional
    @Modifying
    @Query(value = """
        UPDATE RESERVATION R
        JOIN CONCERT_MAPPING CM
        ON R.CONCERT_MAPPING_ID = CM.CONCERT_MAPPING_ID
        AND CM.CONCERT_DATE > NOW()
        SET R.STATUS = 'EXPIRED'
        WHERE R.STATUS = 'AVAILABLE'
        AND R.AVAILABLE_AT < NOW() - INTERVAL 10 MINUTE
    """, nativeQuery = true)
    void updateToExpiredStatus();

    @Query("""
        SELECT COUNT(*)
        FROM Reservation R
        WHERE R.status IN :statuses
        AND R.concertMapping.concertMappingId = :concertMappingId
    """)
    Long retrieveAvailableOrReservedCount(@Param("concertMappingId") Long concertMappingId, @Param("statuses") List<ReservationStatus> statuses);


    @Transactional
    @Modifying
    @Query(value = """
        UPDATE RESERVATION R
        JOIN (
            SELECT RESERVATION_ID
            FROM RESERVATION
            WHERE CONCERT_MAPPING_ID = :concertMappingId AND STATUS = 'WAIT'
            ORDER BY RESERVATION_RANK
            LIMIT :count
        ) subquery
        ON R.RESERVATION_ID = subquery.RESERVATION_ID
        SET R.STATUS = 'AVAILABLE'
    """, nativeQuery = true)
    void updateReservationStatus(@Param("concertMappingId") Long concertMappingId, @Param("count") Long count);
}
