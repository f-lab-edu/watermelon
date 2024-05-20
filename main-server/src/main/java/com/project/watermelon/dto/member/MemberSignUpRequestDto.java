package com.project.watermelon.dto.member;

import com.project.watermelon.model.Member;
import com.project.watermelon.enumeration.MemberRole;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.crypto.password.PasswordEncoder;

@Getter
@Setter
@NoArgsConstructor
public class MemberSignUpRequestDto {

    private String email;
    private String password;
    private String memberName;


    public Member toMember(PasswordEncoder passwordEncoder) {
        return Member.builder()
                .memberName(memberName)
                .password(passwordEncoder.encode(password))
                .memberRole(MemberRole.MEMBER)
                .email(email)
                .build();
    }
}
