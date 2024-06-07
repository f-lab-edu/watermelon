package com.project.watermelon.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    // 400 Bad Request
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "400"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "401"),
    FORBIDDEN(HttpStatus.FORBIDDEN, "403");

    private final HttpStatus httpStatus;
    private final String errorCode;

    ErrorCode(HttpStatus httpStatus, String errorCode) {
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
    }
}
