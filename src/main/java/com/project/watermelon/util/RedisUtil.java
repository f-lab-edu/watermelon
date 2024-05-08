package com.project.watermelon.util;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedisUtil {

    private final RedisTemplate<String, Object> redisTemplate;

// @Autowired -> @RequiredArgsConstructor
//    @Autowired
//    public RedisUtil(RedisTemplate<String, Object> redisTemplate) {
//        this.redisTemplate = redisTemplate;
//    }

    public void saveMessage(String messageId, Object message) {
        redisTemplate.opsForValue().set(messageId, message);
    }

    public Object getMessage(String messageId) {
        return redisTemplate.opsForValue().get(messageId);
    }

    public void saveMessageWithExpiration(String messageId, Object message, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(messageId, message, timeout, unit);
    }
}

