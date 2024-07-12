package com.project.watermelon.security;

import com.project.watermelon.enumeration.MemberRole;
import com.project.watermelon.repository.RedisSecurityContextRepository;
import com.project.watermelon.security.filter.JwtFilter;
import com.project.watermelon.security.jwt.JwtAccessDeniedHandler;
import com.project.watermelon.security.jwt.JwtAuthenticationEntryPoint;
import com.project.watermelon.security.jwt.TokenProvider;
import com.project.watermelon.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final TokenProvider tokenProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final RedisSecurityContextRepository redisSecurityContextRepository;

    @Bean
    @Primary
    public SecurityContextHolderStrategy securityContextHolderStrategy() {
        return redisSecurityContextRepository;
    }

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
                        .requestMatchers("/index.html", "/signup.html", "/login.html").permitAll()
                        .requestMatchers("/css/**", "/js/**").permitAll()
                        .requestMatchers("/members/**").permitAll()
                        .requestMatchers("/seats/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/webapp/resources/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/concerts/**").permitAll()
                        .requestMatchers("/reservations/*").hasAnyAuthority(MemberRole.MEMBER.getAuthority())
                        .requestMatchers("/payments/*").hasAnyAuthority(MemberRole.MEMBER.getAuthority())
                        .anyRequest().hasAuthority(MemberRole.QUALIFIED_MEMBER.getAuthority()))
                .addFilterBefore(new JwtFilter(tokenProvider, redisSecurityContextRepository), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedMethod("*");
        configuration.addAllowedHeader("*");
        configuration.addAllowedOriginPattern("*");
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityUtil securityUtil() {
        return new SecurityUtil(redisSecurityContextRepository);
    }

    @Bean
    public SecureRandom secureRandom() throws NoSuchAlgorithmException, NoSuchProviderException {
        return SecureRandom.getInstance("NativePRNGNonBlocking");
    }

    @Bean
    public PasswordEncoder passwordEncoder(SecureRandom secureRandom) {
        return new BCryptPasswordEncoder(10, secureRandom);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
