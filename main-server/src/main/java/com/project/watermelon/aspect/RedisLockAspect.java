package com.project.watermelon.aspect;

import com.project.watermelon.annotation.RedisLock;
import com.project.watermelon.exception.RedisLockException;
import com.project.watermelon.service.RedisLockService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
@RequiredArgsConstructor
public class RedisLockAspect {

    private final RedisLockService redisLockService;
    private static final int REDIS_TTL_SECONDS = 300;
    private static final String LOCK_PREFIX = "reservationLock:";

    @Around("@annotation(com.project.watermelon.annotation.RedisLock)")
    public Object aroundProcessPayment(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        RedisLock redisLock = method.getAnnotation(RedisLock.class);

        // Annotation 에서 key 필드 가져오기
        String keyExpression = redisLock.key();
        String lockKey = getLockKey(joinPoint, keyExpression);

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

    private String getLockKey(ProceedingJoinPoint joinPoint, String keyExpression) {
        ExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext context = new StandardEvaluationContext();

        // 메서드 인자들을 컨텍스트에 추가
        Object[] args = joinPoint.getArgs();
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String[] parameterNames = methodSignature.getParameterNames();

        for (int i = 0; i < args.length; i++) {
            context.setVariable(parameterNames[i], args[i]);
        }

        // keyExpression 평가
        return LOCK_PREFIX + parser.parseExpression(keyExpression).getValue(context, String.class);
    }
}
