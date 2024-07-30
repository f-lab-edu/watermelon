package com.project.watermelon.service;

import com.project.watermelon.dto.member.MemberLoginRequestDto;
import com.project.watermelon.dto.member.MemberSignUpRequestDto;
import com.project.watermelon.dto.member.MemberSignUpResponseDto;
import com.project.watermelon.dto.token.TokenDto;
import com.project.watermelon.model.Member;
import com.project.watermelon.model.RefreshToken;
import com.project.watermelon.repository.MemberRepository;
import com.project.watermelon.repository.RefreshTokenRepository;
import com.project.watermelon.security.UserDetailsImpl;
import com.project.watermelon.security.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserDetailsCacheService userDetailsCacheService;

    public MemberSignUpResponseDto signup(MemberSignUpRequestDto memberRequestDto) {
        if (memberRepository.existsByEmail(memberRequestDto.getEmail())) {
            String status = "false";
            String message = "duplicate email.";
            return MemberSignUpResponseDto.of(status, message);
        }

        String status = "true";
        String message = "sign up success.";

        Member member = memberRequestDto.toMember(passwordEncoder);
        return MemberSignUpResponseDto.of(memberRepository.save(member), status, message);
    }

    public TokenDto login(MemberLoginRequestDto memberRequestDto) {
        UsernamePasswordAuthenticationToken authenticationToken = memberRequestDto.toAuthentication();
        try {
            long startTime = System.currentTimeMillis();
            log.info("Starting authentication");

            Authentication authentication = userDetailsCacheService.getCachedAuthentication(authenticationToken);
            if (authentication == null) {
                authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
//                authentication = authenticationManager.authenticate(authenticationToken);
                userDetailsCacheService.cacheAuthentication(authenticationToken, authentication);
                // 캐싱된 인증 정보에서 사용자 세부 정보를 캐싱합니다.
                UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
                userDetailsCacheService.cacheUserDetails(userDetails);
            }

            long authTime = System.currentTimeMillis();
            log.info("Authentication completed in {} ms", authTime - startTime);

            TokenDto tokenDto = tokenProvider.generateTokenDto(authentication);

            long tokenTime = System.currentTimeMillis();
            log.info("Token generation completed in {} ms", tokenTime - authTime);

            String status = "true";
            String message = "login success.";
            tokenDto.setStatus(status);
            tokenDto.setMessage(message);

            UserDetailsImpl userDetails = userDetailsCacheService.getCachedUserDetails(authentication.getName());
            if (userDetails == null) {
                userDetails = (UserDetailsImpl) authentication.getPrincipal();
            }
            Member member = userDetails.getMember();

            tokenDto.setMemberId(member.getMemberId());

            // RefreshToken 저장
            RefreshToken refreshToken = RefreshToken.builder()
                    .key(authentication.getName())
                    .value(tokenDto.getRefreshToken())
                    .build();
            refreshTokenRepository.save(refreshToken);

            long endTime = System.currentTimeMillis();
            log.info("Total login process completed in {} ms", endTime - startTime);

            return tokenDto;

        } catch (Exception e) {
            e.printStackTrace();
            String status = "false";
            String message = "check email or password.";
            return new TokenDto(status, message);
        }
    }


}
