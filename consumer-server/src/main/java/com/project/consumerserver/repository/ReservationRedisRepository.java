package com.project.consumerserver.repository;

import com.project.consumerserver.enumeration.ReservationStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ReservationRedisRepository {
    private final StringRedisTemplate stringRedisTemplate;

    public void storeConcertMappingSeatCapacity(String concertMappingId, Long seatCapacity) {
        HashOperations<String, String, String> hashOps = stringRedisTemplate.opsForHash();

        String hashKey = "concertMapping:" + concertMappingId;
        String field = "availableSeatCapacity";

        // 상태를 Hash에 저장
        hashOps.put(hashKey, field, Long.toString(seatCapacity));
    }

    public void decrementConcertMappingSeatCapacity(String concertMappingId) {
        HashOperations<String, String, Long> hashOps = stringRedisTemplate.opsForHash();

        String hashKey = "concertMapping:" + concertMappingId;
        String field = "availableSeatCapacity";

        // 상태를 Hash에 저장
        hashOps.increment(hashKey, field, -1);
    }

    public void updateMemberStatus(String concertMappingId, String memberEmail, ReservationStatus newStatus) {
        HashOperations<String, String, String> hashOps = stringRedisTemplate.opsForHash();

        String hashKey = "concertMappingId:" + concertMappingId + ":memberStatus";

        // 새로운 상태로 업데이트
        hashOps.put(hashKey, memberEmail, newStatus.toString());
    }

    public Long getUserRank(String memberEmail, String concertMappingId) {
        ZSetOperations<String, String> zSetOps = stringRedisTemplate.opsForZSet();
        String zSetKey = "concert:" + concertMappingId + ":members";

        return zSetOps.rank(zSetKey, memberEmail);
    }

    public void deleteUserRank(String concertMappingId, String memberEmail) {
        ZSetOperations<String, String> zSetOps = stringRedisTemplate.opsForZSet();

        String zSetKey = "concertMappingId:" + concertMappingId + ":members";

        // ZSet에서 사용자 제거
        zSetOps.remove(zSetKey, memberEmail);
    }



}
