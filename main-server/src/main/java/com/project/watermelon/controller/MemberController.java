package com.project.watermelon.controller;

import com.project.watermelon.dto.member.MemberLoginRequestDto;
import com.project.watermelon.dto.member.MemberSignUpRequestDto;
import com.project.watermelon.dto.member.MemberSignUpResponseDto;
import com.project.watermelon.dto.token.TokenDto;
import com.project.watermelon.security.SecurityUtil;
import com.project.watermelon.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/signup")
    public ResponseEntity<MemberSignUpResponseDto> signup(@RequestBody MemberSignUpRequestDto memberRequestDto) {
        return ResponseEntity.ok(memberService.signup(memberRequestDto));
    }

    @PostMapping("/login")
    public ResponseEntity<TokenDto> login(@RequestBody MemberLoginRequestDto memberRequestDto) {
        return ResponseEntity.ok(memberService.login(memberRequestDto));
    }

    @GetMapping("/memberName")
    public ResponseEntity<String> retrieveMemberName() {
        return ResponseEntity.ok(memberService.retrieveMemberName());
    }

}
