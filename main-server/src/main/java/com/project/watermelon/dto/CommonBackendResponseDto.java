package com.project.watermelon.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommonBackendResponseDto<T> {
    private String status = "ok"; // 응답 상태를 나타내는 필드, 예: "ok", "fail"
    private T data = null; // 실제 응답 데이터를 포함할 필드
    private String reason = null; // 실제 응답 데이터를 포함할 필드

    // 기본 생성자
    public CommonBackendResponseDto() {
    }

    // 모든 필드를 초기화하는 생성자
    public CommonBackendResponseDto(String status, T data) {
        this.status = status;
        this.data = data;
    }

    // 에러 발생 시 상태를 "fail"로 설정하는 메서드
    public void markAsFailed(String reason) {
        this.status = "fail";
        this.reason = reason;
    }
}
