package com.project.consumerserver.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;

@Repository
@RequiredArgsConstructor
public class ReservationRepository {

    private final JdbcTemplate jdbcTemplate;

    public int createReservation(Long concertMappingId, Long memberId, Long reservationRank, String status) {
        String sql = "INSERT INTO RESERVATION (CONCERT_MAPPING_ID, MEMBER_ID, RESERVATION_RANK, STATUS, CREATED_AT, MODIFIED_AT) VALUES (?, ?, ?, ?, ?, ?)";
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        return jdbcTemplate.update(sql, concertMappingId, memberId, reservationRank, status, currentTime, currentTime);
    }
}
