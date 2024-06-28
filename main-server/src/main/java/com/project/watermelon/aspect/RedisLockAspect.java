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

    @Around("@annotation(com.project.watermelon.annotation.RedisLock) && args(reservationId,..)")
    public Object aroundProcessPayment(ProceedingJoinPoint joinPoint, Long reservationId) throws Throwable {
        String lockKey = "reservationLock:" + reservationId;
        try {
            boolean locked = redisLockService.lock(lockKey, 300);
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
}
