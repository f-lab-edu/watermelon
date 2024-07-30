package com.project.watermelon.repository;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.stereotype.Component;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import java.util.concurrent.TimeUnit;

@Component
public class RedisSecurityContextRepository implements SecurityContextHolderStrategy {
    private final RedisTemplate<String, Object> redisTemplate;
    private static final String REDIS_KEY_PREFIX = "SECURITY_CONTEXT_";

    public RedisSecurityContextRepository(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void clearContext() {
        String key = getKey();
        redisTemplate.delete(key);
    }

    @Override
    public SecurityContext getContext() {
        String key = getKey();
        Object contextObject = redisTemplate.opsForValue().get(key);
        SecurityContext context;

        try {
            if (contextObject instanceof Map) {
                Map<String, Object> contextMap = (Map<String, Object>) contextObject;
                if (contextMap.containsKey("authentication")) {
                    Map<String, Object> authMap = (Map<String, Object>) contextMap.get("authentication");

                    // authorities 리스트를 올바르게 변환
                    List<SimpleGrantedAuthority> authorities = ((List<Map<String, String>>) authMap.get("authorities")).stream()
                            .map(authorityMap -> new SimpleGrantedAuthority(authorityMap.get("authority")))
                            .collect(Collectors.toList());

                    // principal 값을 올바르게 변환
                    Map<String, Object> principalMap = (Map<String, Object>) authMap.get("principal");
                    String principal = (String) principalMap.get("username");  // principal에서 username 추출

                    String credentials = (String) authMap.get("credentials");
                    boolean authenticated = (Boolean) authMap.get("authenticated");

                    // UsernamePasswordAuthenticationToken 생성
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(principal, credentials, authorities);

                    context = SecurityContextHolder.createEmptyContext();
                    context.setAuthentication(authentication);
                } else {
                    context = SecurityContextHolder.createEmptyContext();
                }
            } else {
                context = SecurityContextHolder.createEmptyContext();
                redisTemplate.opsForValue().set(key, context, 10, TimeUnit.MINUTES);
            }
        } catch (ClassCastException e) {
            // ClassCastException 예외 처리
            context = SecurityContextHolder.createEmptyContext();
        }

        return context;
    }

    @Override
    public void setContext(SecurityContext context) {
        String key = getKey();
        redisTemplate.opsForValue().set(key, context, 10, TimeUnit.MINUTES);
    }

    @Override
    public SecurityContext createEmptyContext() {
        return SecurityContextHolder.createEmptyContext();
    }

    private String getKey() {
        return REDIS_KEY_PREFIX + Thread.currentThread().getId();
    }
}
