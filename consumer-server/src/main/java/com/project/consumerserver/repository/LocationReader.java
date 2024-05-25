package com.project.consumerserver.repository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@Repository
public class LocationReader {

    // MySQL 접속 정보
    @Value("${spring.datasource.url}")
    private String JDBC_URL;

    @Value("${spring.datasource.username}")
    private String JDBC_USER;

    @Value("${spring.datasource.password}")
    private String JDBC_PASSWORD;

    public Long retrieveLocationSeatCapacity(Long locationId) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            // 1. JDBC 드라이버 로드
            Class.forName("com.mysql.cj.jdbc.Driver");

            // 2. 데이터베이스 연결
            connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);

            // 3. SQL 쿼리 실행
            String sql = "SELECT SEAT_CAPACITY FROM LOCATION WHERE LOCATION_ID = ?"; // 실행할 SQL 쿼리
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setLong(1, locationId); // 파라미터 바인딩

            // 4. 결과 집합 가져오기
            resultSet = preparedStatement.executeQuery();

            // 5. 결과 처리
            if (resultSet.next()) {
                return resultSet.getLong("SEAT_CAPACITY");
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 리소스 해제
            try {
                if (resultSet != null) resultSet.close();
                if (preparedStatement != null) preparedStatement.close();
                if (connection != null) connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null; // 예외 발생 시 또는 결과가 없을 때 null 반환
    }
}
