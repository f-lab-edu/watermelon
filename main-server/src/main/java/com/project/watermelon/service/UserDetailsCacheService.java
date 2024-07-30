package com.project.watermelon.service;

import com.project.watermelon.security.UserDetailsImpl;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsCacheService {

    @Cacheable(value = "authenticationCache", key = "#authenticationToken.name")
    public Authentication getCachedAuthentication(UsernamePasswordAuthenticationToken authenticationToken) {
        return null; // 캐시에 없으면 null 반환
    }

    @CachePut(value = "authenticationCache", key = "#authenticationToken.name")
    public Authentication cacheAuthentication(UsernamePasswordAuthenticationToken authenticationToken, Authentication authentication) {
        return authentication;
    }

    @Cacheable(value = "userDetailsCache", key = "#username")
    public UserDetailsImpl getCachedUserDetails(String username) {
        // 이 메서드는 인증 객체에서 사용자 이름을 기반으로 UserDetails를 캐싱합니다.
        return null; // 캐시에 없으면 null 반환
    }

    @CachePut(value = "userDetailsCache", key = "#userDetails.username")
    public UserDetailsImpl cacheUserDetails(UserDetailsImpl userDetails) {
        return userDetails;
    }
}
