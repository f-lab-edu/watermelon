package com.project.watermelon.util;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExpiredKeyListener extends MessageListenerAdapter {

    private StringRedisTemplate redisTemplate;
    private static final String LOCK_PREFIX = "reservationLock:";

    @Override
    public void onMessage(org.springframework.data.redis.connection.Message message, byte[] pattern) {
        String expiredKey = message.toString();

        if (expiredKey.startsWith(LOCK_PREFIX)) {
            String reservationId = expiredKey.substring(LOCK_PREFIX.length());
            String lockedReservationsKey = "lockedReservations";

            // Remove the reservation ID from the set of locked reservations
            redisTemplate.opsForSet().remove(lockedReservationsKey, reservationId);
        }
    }
}

