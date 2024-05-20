package com.project.watermelon.dto.token;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenDto {
    private String status;
    private String message;
    private Long memberId;

    private String grantType;
    private String accessToken;
    private String refreshToken;
    private Long accessTokenExpiresIn;

    public TokenDto(String status, String message){
        this.status = status;
        this.message = message;
    }
}
