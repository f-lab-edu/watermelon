package com.project.watermelon.repository;


import com.project.watermelon.enumeration.ReservationStatus;
import com.project.watermelon.model.Reservation;
import com.project.watermelon.vo.ConcertMappingSeatInfoVO;
import com.project.watermelon.vo.ReservationSeatVo;
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
        SELECT DISTINCT new com.project.watermelon.vo.ConcertMappingSeatInfoVO(C.concertMappingId, L.seatCapacity)
        FROM Reservation R
        JOIN R.concertMapping C
        JOIN C.location L
        WHERE C.concertDate > CURRENT_TIMESTAMP
    """)
    List<ConcertMappingSeatInfoVO> retrieveConcertMappingSeatCapacities();
    // CURRENT_TIMESTAMP -> 어플리케이션에서 넣어주기
    // DISTINCT -> 여러개 걸리면 조합으로 걸림 -> DISTINCT 필요 없을 것 같음.
    // Named query

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
    // now 를 어플리케이션에서 넣어서 넣어주기 -> 데이터베이스 내장 함수 사용 시 인덱스를 먹지 않음!

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
        SET
            R.STATUS = 'AVAILABLE',
            R.AVAILABLE_AT = NOW()
    """, nativeQuery = true)
    void updateReservationStatus(@Param("concertMappingId") Long concertMappingId, @Param("count") Long count);
    // NOW() 빼고 어플리케이션 레벨에서 넣어주기.
    // JOIN -> 종류 별 성능 다른점
    // 셀프 조인을 쓰는 방식은 일반적이지 않아서 -> 제너럴한 쿼리 공부해보기! <- 로직 자체는 괜찮음!
    // JPA 로 사용하는게 훨씬 깔끔할 것으로 보임!
    // 아래 두 어노테이션 사용하는 이유에 대해서 공부!
    // @Transactional
    // @Modifying

    @Query("""
    SELECT new com.project.watermelon.vo.ReservationSeatVo(R.reservationId, T.ticketId, T.seat.seatId)
    FROM Reservation R
    JOIN Ticket T
    ON R.ticketId = T.ticketId
    WHERE R.concertMapping.concertMappingId = :concertMappingId
    AND R.status IN :statuses
    """)
    List<ReservationSeatVo> findByConcertMappingIdAndStatuses(@Param("concertMappingId") Long concertMappingId,
                                                     @Param("statuses") List<ReservationStatus> statuses);

}
