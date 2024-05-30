package com.project.consumerserver.repository;

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

    public List<Long> retrieveUniqueConcertMappingIds() {
        String sql = """
        SELECT DISTINCT
            CONCERT_MAPPING_ID
        FROM RESERVATION
        """;

        RowMapper<Long> rowMapper = (resultSet, rowNum) -> resultSet.getLong("CONCERT_MAPPING_ID");
        return jdbcTemplate.query(sql, rowMapper);
    }

    public Long retrieveConcertMappingSeatCapacity(Long concertMappingId) {
        String sql = """
        SELECT
            L.SEAT_CAPACITY
        FROM LOCATION L
        INNER JOIN CONCERT_MAPPING C
        ON L.LOCATION_ID=C.LOCATION_ID
        WHERE CONCERT_MAPPING_ID = ?
        """;

        RowMapper<Long> rowMapper = (resultSet, rowNum) -> resultSet.getLong("SEAT_CAPACITY");
        return jdbcTemplate.queryForObject(sql, rowMapper, concertMappingId);
    }
}
