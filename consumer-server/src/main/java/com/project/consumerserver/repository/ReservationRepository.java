package com.project.consumerserver.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ReservationRepository {

    private final JdbcTemplate jdbcTemplate;

    public int createReservation(Long concertMappingId, Long memberId, Long rank, String status) {
        String sql = "INSERT INTO RESERVATION (CONCERT_MAPPING_ID, MEMBER_ID, RANK, STATUS) VALUES (?, ?, ?, ?)";

        return jdbcTemplate.update(sql, concertMappingId, memberId, rank, status);
    }
}
