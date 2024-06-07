package com.project.consumerserver.repository;

import com.project.consumerserver.enumeration.ReservationStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;
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
