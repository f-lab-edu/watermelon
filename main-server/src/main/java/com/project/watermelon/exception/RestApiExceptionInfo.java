package com.project.watermelon.exception;

import org.springframework.http.HttpStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RestApiExceptionInfo {
    private HttpStatus httpStatus;
    private String errorMessage;
    private String errorCode;
}
