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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final RefreshTokenRepository refreshTokenRepository;

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
        // 1. Login ID/PW 를 기반으로 AuthenticationToken 생성
        UsernamePasswordAuthenticationToken authenticationToken = memberRequestDto.toAuthentication();
        try {
            // 2. 실제로 검증 (사용자 비밀번호 체크) 이 이루어지는 부분
            // authenticate 메서드가 실행이 될 때 UserDetailsServiceImpl 에서 만들었던 loadUserByUsername 메서드가 실행됨
            Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

            // 3. 인증 정보를 기반으로 JWT 토큰 생성
            TokenDto tokenDto = tokenProvider.generateTokenDto(authentication);

            // tokenDto 값 set
            String status = "true";
            String message = "login success.";
            tokenDto.setStatus(status);
            tokenDto.setMessage(message);

            // 여기에선 인증 정보가 UserDetailsImpl로 저장되어 Member 객체를 바로 사용 가능 (쿼리 감소)
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            Member member = userDetails.getMember();

            tokenDto.setMemberId(member.getMemberId());

            // 4. RefreshToken 저장
            RefreshToken refreshToken = RefreshToken.builder()
                    .key(authentication.getName())
                    .value(tokenDto.getRefreshToken())
                    .build();

            refreshTokenRepository.save(refreshToken);

            // 5. 토큰 발급
            return tokenDto;

        }catch (Exception e){
            e.printStackTrace();
            String status = "false";
            String message = "check email or password.";
            return new TokenDto(status, message);
        }
    }
}
