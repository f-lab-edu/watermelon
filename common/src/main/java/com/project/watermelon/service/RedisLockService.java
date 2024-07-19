package com.project.watermelon.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RedisLockService {

    private final StringRedisTemplate stringRedisTemplate;

    public boolean lock(String key, long ttlInSeconds) {
        return Boolean.TRUE.equals(stringRedisTemplate.execute(new SessionCallback<Boolean>() {
            @Override
            @SuppressWarnings("unchecked")
            public Boolean execute(@NonNull RedisOperations operations) throws DataAccessException {
                // WATCH 명령어로 특정 키를 모니터링
                operations.watch(key);

                // SETNX (setIfAbsent)로 락 설정 시도
                Boolean success = operations.opsForValue().setIfAbsent(key, "locked");

                if (Boolean.FALSE.equals(success)) {
                    // 락 설정에 실패한 경우, UNWATCH 명령어로 모니터링 해제
                    operations.unwatch();
                    return false;
                }

                // 트랜잭션 시작 (MULTI)
                operations.multi();

                // 트랜잭션 내에서 TTL 설정
                operations.expire(key, ttlInSeconds, TimeUnit.SECONDS);

                // 트랜잭션 커밋 (EXEC)
                List<Object> exec = operations.exec();

                // 트랜잭션 결과 확인
                return exec != null && !exec.isEmpty();
            }
        }));
    }

    public void unlock(String key) {
        stringRedisTemplate.delete(key);
    }
}
