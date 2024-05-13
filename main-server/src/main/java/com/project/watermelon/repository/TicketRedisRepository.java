package com.project.watermelon.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
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

}
