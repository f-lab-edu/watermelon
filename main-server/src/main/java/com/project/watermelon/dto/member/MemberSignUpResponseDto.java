package com.project.watermelon.dto.member;

import com.project.watermelon.dto.CommonBackendResponseDto;
import com.project.watermelon.model.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MemberSignUpResponseDto {

    private String username;
    private String status;
    private String message;

    public static MemberSignUpResponseDto of(Member member, String status, String message) {
        return new MemberSignUpResponseDto(member.getMemberName(), status, message);
    }

    public static MemberSignUpResponseDto of(String status, String message) {
        return new MemberSignUpResponseDto("",status, message);
    }
}
