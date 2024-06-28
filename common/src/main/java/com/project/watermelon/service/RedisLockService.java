package com.project.watermelon.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisLockService {

    private final StringRedisTemplate stringRedisTemplate;

    public boolean lock(String key, long ttlInSeconds) {
        // 메인 서버의 동시성 이슈 및 중복 처리 방지를 위해 SETNX (setIfAbsent) 사용
        Boolean success = stringRedisTemplate.opsForValue().setIfAbsent(
                key, "locked",
                ttlInSeconds,
                TimeUnit.SECONDS
        );
        if (Boolean.TRUE.equals(success)) {
            addLockedReservationId("lockedReservationList", key);
        }
        return Boolean.TRUE.equals(success);
    }

    public void unlock(String key) {
        stringRedisTemplate.delete(key);
        removeLockedReservationId("lockedReservationList", key);
    }

    private void addLockedReservationId(String setKey, String reservationId) {
        stringRedisTemplate.opsForSet().add(setKey, reservationId);
    }

    private void removeLockedReservationId(String setKey, String reservationId) {
        stringRedisTemplate.opsForSet().remove(setKey, reservationId);
    }
}
