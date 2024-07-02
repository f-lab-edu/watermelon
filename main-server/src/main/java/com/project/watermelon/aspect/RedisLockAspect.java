package com.project.watermelon.aspect;

import com.project.watermelon.annotation.RedisLock;
import com.project.watermelon.exception.RedisLockException;
import com.project.watermelon.model.LockKey;
import com.project.watermelon.service.RedisLockService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
@RequiredArgsConstructor
public class RedisLockAspect {

    private final RedisLockService redisLockService;
    private static final int REDIS_TTL_SECONDS = 300;

    @Around("@annotation(com.project.watermelon.annotation.RedisLock)")
    public Object aroundProcessPayment(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        RedisLock redisLock = method.getAnnotation(RedisLock.class);

        // Annotation에서 keyType 필드와 lockPrefix 필드를 가져오기
        Class<?> keyType = redisLock.keyType();
        String lockPrefix = redisLock.lockPrefix();
        Object lockKeyObject = getLockKeyObject(joinPoint, keyType);

        String lockKey = lockPrefix + getLockKeyFromObject(lockKeyObject);

        try {
            boolean locked = redisLockService.lock(lockKey, REDIS_TTL_SECONDS);
            if (!locked) {
                throw new RedisLockException("Failed to acquire lock for key: " + lockKey);
            }
            return joinPoint.proceed();
        } catch (Exception e) {
            throw new RedisLockException("Failed to acquire lock for key: " + lockKey);
        } finally {
            redisLockService.unlock(lockKey);
        }
    }

    private Object getLockKeyObject(ProceedingJoinPoint joinPoint, Class<?> keyType) {
        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            if (keyType.isInstance(arg)) {
                return arg;
            }
        }
        throw new IllegalArgumentException("No parameter of type " + keyType.getName() + " found in method arguments.");
    }

    private String getLockKeyFromObject(Object lockKeyObject) {
        if (lockKeyObject instanceof LockKey) {
            return ((LockKey) lockKeyObject).getLockKey().toString();
        }
        throw new IllegalArgumentException("Unsupported key type: " + lockKeyObject.getClass().getName());
    }
}
