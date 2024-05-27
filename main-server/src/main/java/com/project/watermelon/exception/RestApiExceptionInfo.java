package com.project.watermelon.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class RestApiExceptionInfo{

    private String errorMessage;
    private HttpStatus httpStatus;

}