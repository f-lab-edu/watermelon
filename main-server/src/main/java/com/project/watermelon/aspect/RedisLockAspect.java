package com.project.watermelon.aspect;

import com.project.watermelon.exception.RedisLockException;
import com.project.watermelon.service.RedisLockService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class RedisLockAspect {

    private final RedisLockService redisLockService;
    private static final int REDIS_TTL_SECONDS = 300;

    @Around("@annotation(com.project.watermelon.annotation.RedisLock) && args(reservationId,..)")
    public Object aroundProcessPayment(ProceedingJoinPoint joinPoint, Long reservationId) throws Throwable {
        String lockKey = getLockKey(reservationId);
        try {
            boolean locked = redisLockService.lock(lockKey, REDIS_TTL_SECONDS);
            if (!locked) {
                throw new RedisLockException("Failed to acquire lock for reservation: " + reservationId);
            }
            return joinPoint.proceed();
        } catch (Exception e) {
            throw new RedisLockException("Failed to acquire lock for reservation: " + reservationId);
        } finally {
            redisLockService.unlock(lockKey);
        }
    }

    private String getLockKey(Long reservationId) {
        // 추가적인 더하기 연산이 발생할 경우 StringBuilder 를 사용하여 메모리 효율성 개선
        return "reservationLock:" + reservationId;
    }
}
