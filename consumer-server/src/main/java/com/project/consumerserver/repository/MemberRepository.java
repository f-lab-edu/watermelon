package com.project.consumerserver.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MemberRepository {

    private final JdbcTemplate jdbcTemplate;

    public Long retrieveMemberIdByEmail(String email) {
        String sql = "SELECT MEMBER_ID FROM MEMBER WHERE EMAIL = ?";

        RowMapper<Long> rowMapper = (resultSet, rowNum) -> resultSet.getLong("MEMBER_ID");

        return jdbcTemplate.queryForObject(sql, rowMapper, email);
    }
}
