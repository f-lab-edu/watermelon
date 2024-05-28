package com.project.consumerserver.repository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@Repository
public class ReservationManager {

    // MySQL 접속 정보
    @Value("${spring.datasource.url}")
    private String JDBC_URL;

    @Value("${spring.datasource.username}")
    private String JDBC_USER;

    @Value("${spring.datasource.password}")
    private String JDBC_PASSWORD;

    public void retrieveLocationSeatCapacity(Long stringConcertMappingId, Long memberId, Long rank, String status) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            // 1. JDBC 드라이버 로드
            Class.forName("com.mysql.cj.jdbc.Driver");

            // 2. 데이터베이스 연결
            connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);

            // 3. SQL 쿼리 실행
            String sql = "INSERT INTO RESERVATION (CONCERT_MAPPING_ID, MEMBER_ID, RANK, STATUS) VALUES (?, ?, ?, ?)"; // 실행할 SQL 쿼리
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setLong(1, stringConcertMappingId); // 파라미터 바인딩
            preparedStatement.setLong(2, memberId); // 파라미터 바인딩
            preparedStatement.setLong(3, rank); // 파라미터 바인딩
            preparedStatement.setString(4, status); // 파라미터 바인딩

            preparedStatement.executeQuery();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 리소스 해제
            try {
                if (preparedStatement != null) preparedStatement.close();
                if (connection != null) connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
