package com.project.consumerserver.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class LocationRepository {

    private final JdbcTemplate jdbcTemplate;

    public Long retrieveLocationSeatCapacity(Long locationId) {
        String sql = """
        SELECT
            SEAT_CAPACITY
        FROM LOCATION
        WHERE LOCATION_ID = ?
        """;

        RowMapper<Long> rowMapper = (resultSet, rowNum) -> resultSet.getLong("SEAT_CAPACITY");

        return jdbcTemplate.queryForObject(sql, rowMapper, locationId);
    }
}
