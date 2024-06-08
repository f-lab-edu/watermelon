package com.project.consumerserver.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ReservationRedisRepository {
    private final StringRedisTemplate stringRedisTemplate;

    public Long incrementReservationRankAndReturn(String concertMappingId) {
        String key = "concertMapping:" + concertMappingId + ":rank";
        return stringRedisTemplate.opsForValue().increment(key, 1);
    }
}
