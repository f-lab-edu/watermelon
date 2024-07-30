//package com.project.watermelon.config;
//
//import com.project.watermelon.repository.RedisSecurityContextRepository;
//import com.project.watermelon.security.SecurityUtil;
//import lombok.RequiredArgsConstructor;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//
//import java.security.NoSuchAlgorithmException;
//import java.security.NoSuchProviderException;
//import java.security.SecureRandom;
//
//@Configuration
//@RequiredArgsConstructor
//public class SecurityConfig {
//
//    private final RedisSecurityContextRepository redisSecurityContextRepository;
//
//    @Bean
//    public SecurityUtil securityUtil() {
//        return new SecurityUtil(redisSecurityContextRepository);
//    }
//
//    @Bean
//    public SecureRandom secureRandom() throws NoSuchAlgorithmException, NoSuchProviderException {
//        // 명시적으로 /dev/urandom을 사용하도록 설정
//        return SecureRandom.getInstance("NativePRNGNonBlocking");
//    }
//
//    @Bean
//    public PasswordEncoder passwordEncoder(SecureRandom secureRandom) {
//        return new BCryptPasswordEncoder(10, secureRandom); // SecureRandom 인스턴스를 전달
//    }
//}
