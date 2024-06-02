package com.project.consumerserver.repository;

import com.project.consumerserver.dto.ConcertMappingSeatInfoDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ReservationRepository {

    private final JdbcTemplate jdbcTemplate;

    public void createReservation(Long concertMappingId, Long memberId, Long reservationRank, String status) {
        String sql = """
        INSERT INTO RESERVATION (CONCERT_MAPPING_ID, MEMBER_ID, RESERVATION_RANK, STATUS, CREATED_AT, MODIFIED_AT)
        VALUES (?, ?, ?, ?, ?, ?)
        """;
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        jdbcTemplate.update(sql, concertMappingId, memberId, reservationRank, status, currentTime, currentTime);
    }
}
