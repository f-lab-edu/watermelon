package com.project.watermelon.repository;

import com.project.watermelon.enumeration.ReservationStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class ReservationRedisRepository {
    private final StringRedisTemplate stringRedisTemplate;

    public void storeUserIdWithDefaultState(String memberEmail, String concertMappingId) {
        HashOperations<String, String, String> hashOps = stringRedisTemplate.opsForHash();

        String hashKey = "concertMappingId:" + concertMappingId + ":memberStatus";

        // 상태를 Hash에 저장
        hashOps.put(hashKey, memberEmail, ReservationStatus.WAIT.toString());

        // TTL을 10분으로 설정 (10분 = 600초)
        stringRedisTemplate.expire(hashKey, 10, TimeUnit.MINUTES);
    }


    public Long getUserRank(String memberEmail, String concertMappingId) {
        ZSetOperations<String, String> zSetOps = stringRedisTemplate.opsForZSet();
        String zSetKey = "concert:" + concertMappingId + ":members";

        return zSetOps.rank(zSetKey, memberEmail);
    }

    public void deleteUserRank(String userId) {
        ZSetOperations<String, String> zSetOps = stringRedisTemplate.opsForZSet();

        zSetOps.remove("userRankings", userId);

        // 사용자 ID와 관련된 추가 데이터 삭제 (예: Redis String Key)
        stringRedisTemplate.opsForValue().getOperations().delete("userId:" + userId);
    }


}
