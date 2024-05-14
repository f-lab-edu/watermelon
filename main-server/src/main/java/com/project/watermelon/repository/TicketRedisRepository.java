package com.project.watermelon.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TicketRedisRepository {
    private final StringRedisTemplate stringRedisTemplate;

    public Boolean checkTicketPublishCountAvailability() {
        ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
        String maxAvailableProgressCountKey = "maxAvailableProgressCount";
        String currentProgressCountKey = "currentProgressCount";
        int maxAvailableCount = 0;
        int currentProgressCount = 0;

        String maxAvailableCountValue = ops.get(maxAvailableProgressCountKey);
        if (maxAvailableCountValue != null) {
            maxAvailableCount = Integer.parseInt(maxAvailableCountValue);
        }

        String currentProgressCountValue = ops.get(currentProgressCountKey);
        if (currentProgressCountValue != null) {
            currentProgressCount = Integer.parseInt(currentProgressCountValue);
        }

        return maxAvailableCount >= currentProgressCount;
    }

    public void storeUserIdWithCurrentTimeScore(String userId) {
        ZSetOperations<String, String> zSetOps = stringRedisTemplate.opsForZSet();
        long currentTimeMillis = System.currentTimeMillis();
        zSetOps.add("userRankings", userId, (double) currentTimeMillis);
    }


    public Long getUserRank(String userId) {
        return stringRedisTemplate.opsForZSet().rank("userRankings", userId);
    }

    public void deleteUserRank(String userId) {
        ZSetOperations<String, String> zSetOps = stringRedisTemplate.opsForZSet();

        zSetOps.remove("userRankings", userId);

        // 사용자 ID와 관련된 추가 데이터 삭제 (예: Redis String Key)
        stringRedisTemplate.opsForValue().getOperations().delete("userId:" + userId);
    }


}
