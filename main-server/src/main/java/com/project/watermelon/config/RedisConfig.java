package com.project.watermelon.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import jakarta.annotation.PostConstruct;

@Configuration
@RequiredArgsConstructor
public class RedisConfig {

    private final StringRedisTemplate stringRedisTemplate;

    @Value("${redis.maxAvailableProgressCount}")
    private String maxAvailableProgressCount;

    @PostConstruct
    public void initializeDefaults() {
        ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
        String maxAvailableProgressCountKey = "maxAvailableProgressCount";
        String currentProgressCountKey = "currentProgressCount";

        // maxAvailableProgressCount 설정
        String maxAvailable = ops.get(maxAvailableProgressCountKey);
        if (maxAvailable == null) {
            maxAvailable = this.maxAvailableProgressCount; // 기본값
            ops.set(maxAvailableProgressCountKey, maxAvailable);
        }

        // currentProgressCount 설정
        String currentProgress = ops.get(currentProgressCountKey);
        if (currentProgress == null) {
            currentProgress = "0"; // 기본값
            ops.set(currentProgressCountKey, currentProgress);
        }

        System.out.println("Max Available Progress Count: " + maxAvailable);
        System.out.println("Current Progress Count: " + currentProgress);
    }

    @Bean
    public RedisTemplate<String, Object> ticketTemplate(RedisConnectionFactory connectionFactory, ObjectMapper objectMapper) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        // objectMapper 대신 Ticket과 같이 정의된 객체로 변경할 예정
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer(objectMapper)); // 사용자 정의 ObjectMapper를 사용
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer(objectMapper)); // 사용자 정의 ObjectMapper를 사용
        template.afterPropertiesSet();
        return template;
    }
}

