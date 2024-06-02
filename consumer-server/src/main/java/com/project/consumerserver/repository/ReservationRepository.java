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

    public List<Long> retrieveUniqueConcertMappingIds() {
        String sql = """
        SELECT DISTINCT
            CONCERT_MAPPING_ID
        FROM RESERVATION
        """;

        RowMapper<Long> rowMapper = (resultSet, rowNum) -> resultSet.getLong("CONCERT_MAPPING_ID");
        return jdbcTemplate.query(sql, rowMapper);
    }

    public List<ConcertMappingSeatInfoDTO> retrieveConcertMappingSeatCapacities() {
        String sql = """
        SELECT DISTINCT
            C.CONCERT_MAPPING_ID,
            L.SEAT_CAPACITY
        FROM RESERVATION R
        INNER JOIN CONCERT_MAPPING C
        ON R.CONCERT_MAPPING_ID = C.CONCERT_MAPPING_ID
        INNER JOIN LOCATION L
        ON C.LOCATION_ID = L.LOCATION_ID
        """;

        RowMapper<ConcertMappingSeatInfoDTO> rowMapper = (resultSet, rowNum) -> {
            ConcertMappingSeatInfoDTO info = new ConcertMappingSeatInfoDTO();
            info.setConcertMappingId(resultSet.getLong("CONCERT_MAPPING_ID"));
            info.setSeatCapacity(resultSet.getLong("SEAT_CAPACITY"));
            return info;
        };

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

    public Long retrieveAvailableOrReservedCount(Long concertMappingId) {
        String sql = """
        SELECT
            COUNT(*) AS COUNT
        FROM RESERVATION
        WHERE CONCERT_MAPPING_ID = ?
        AND STATUS IN ('AVAILABLE', 'RESERVED')
        """;

        return jdbcTemplate.queryForObject(sql, Long.class, concertMappingId);
    }

    public void updateReservationStatus(Long concertMappingId, Long updateCount) {
        String sql = """
        UPDATE RESERVATION
        SET STATUS = 'AVAILABLE'
        WHERE RESERVATION_ID IN (
            SELECT RESERVATION_ID
            FROM (
                SELECT RESERVATION_ID
                FROM RESERVATION
                WHERE CONCERT_MAPPING_ID = ? AND STATUS = 'WAIT'
                ORDER BY RESERVATION_RANK ASC
                LIMIT ?
            ) AS subquery
        )
        """;
        jdbcTemplate.update(sql, concertMappingId, updateCount);
    }
}
