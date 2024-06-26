package com.project.watermelon.security;

import com.project.watermelon.enumeration.MemberRole;
import com.project.watermelon.security.filter.JwtFilter;
import com.project.watermelon.security.jwt.JwtAccessDeniedHandler;
import com.project.watermelon.security.jwt.JwtAuthenticationEntryPoint;
import com.project.watermelon.security.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final TokenProvider tokenProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                        .accessDeniedHandler(jwtAccessDeniedHandler))
                .headers(headers -> headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth

//                        .requestMatchers("/**").permitAll()) // for local debug

                        .requestMatchers("/index.html","/signup.html", "/login.html").permitAll() // 회원가입, 로그인 페이지 허용
                        .requestMatchers("/css/**", "/js/**").permitAll() // CSS, JavaScript 파일 허용
                        .requestMatchers("/members/**").permitAll()
                        .requestMatchers("/seats/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/webapp/resources/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/concerts/**").permitAll()
                        .requestMatchers("/reservations/*").hasAnyAuthority(MemberRole.MEMBER.getAuthority())
                        .requestMatchers("/payments/*").hasAnyAuthority(MemberRole.MEMBER.getAuthority())
                        .anyRequest().hasAuthority(MemberRole.QUALIFIED_MEMBER.getAuthority()))

                .addFilterBefore(new JwtFilter(tokenProvider), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedMethod("*");
        configuration.addAllowedHeader("*");
        configuration.addAllowedOriginPattern("*"); // 특정 프론트엔드 URL로 지정하는 것을 권장
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
