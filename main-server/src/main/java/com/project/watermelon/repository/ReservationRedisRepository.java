package com.project.watermelon.repository;

import com.project.watermelon.enumeration.ReservationStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class ReservationRedisRepository {
    private final StringRedisTemplate stringRedisTemplate;

    // 공통 키 생성 메서드
    private String createHashKeyForMemberStatus(String concertMappingId) {
        return "concertMappingId:" + concertMappingId + ":memberStatus";
    }

    public void storeUserIdWithDefaultState(String memberEmail, String concertMappingId) {
        HashOperations<String, String, String> hashOps = stringRedisTemplate.opsForHash();

        String hashKey = createHashKeyForMemberStatus(concertMappingId);

        // 상태를 Hash에 저장
        hashOps.put(hashKey, memberEmail, ReservationStatus.WAIT.toString());

        // TTL을 10분으로 설정 (10분 = 600초)
        stringRedisTemplate.expire(hashKey, 10, TimeUnit.MINUTES);
    }

    // Lock the reservation with a TTL
    public void lockReservation(Long reservationId, long ttlInSeconds) {
        ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
        String key = "reservationLock:" + reservationId;
        ops.set(key, "locked", ttlInSeconds, TimeUnit.SECONDS);
    }

    // Add the reservation ID to the set of locked reservations
    public void addLockedReservationId(String key, Long reservationId) {
        stringRedisTemplate.opsForSet().add(key, reservationId.toString());
    }

}
