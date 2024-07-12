package com.project.watermelon.security;

import com.project.watermelon.repository.RedisSecurityContextRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SecurityUtil {

    private final RedisSecurityContextRepository redisSecurityContextRepository;

    // SecurityContext 에 유저 정보가 저장되는 시점
    // Request 가 들어올 때 JwtFilter 의 doFilter 에서 저장
    public String getCurrentMemberUsername() {
        Object tt = redisSecurityContextRepository.getContext();
        final Authentication authentication = redisSecurityContextRepository.getContext().getAuthentication();

        if (authentication == null || authentication.getName() == null) {
            throw new NullPointerException("Security Context에 인증 정보가 없습니다.");
        }
        //authenticaion은 principal을 extends 받은 객체. getName() 메서드는 사용자의 이름을 넘겨준다.
        //String email
        return authentication.getName();
    }
}
