package com.project.watermelon.exception;

import org.apache.tomcat.util.http.fileupload.impl.SizeLimitExceededException;
import org.hibernate.PropertyValueException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.support.MissingServletRequestPartException;


@RestControllerAdvice // Json형태로 restApiException Body 부분에 태워 보내준다.
public class RestApiExceptionHandler {

    @ExceptionHandler(value = { IllegalArgumentException.class})
    public ResponseEntity<Object> handleApiRequestException(IllegalArgumentException ex) {
        RestApiExceptionInfo restApiExceptionInfo = new RestApiExceptionInfo();

        ErrorCode errorCode = ErrorCode.ILLEGAL_ARGUMENT_EXCEPTION;

        restApiExceptionInfo.setHttpStatus(errorCode.getHttpStatus());
        restApiExceptionInfo.setErrorMessage(ex.getMessage());

        return new ResponseEntity(restApiExceptionInfo, restApiExceptionInfo.getHttpStatus());
    }

    @ExceptionHandler(value = { NullPointerException.class})
    public ResponseEntity<Object> handleApiRequestException(NullPointerException ex) {
        RestApiExceptionInfo restApiExceptionInfo = new RestApiExceptionInfo();

        ErrorCode errorCode = ErrorCode.NULL_POINTER_EXCEPTION;

        restApiExceptionInfo.setHttpStatus(errorCode.getHttpStatus());
        restApiExceptionInfo.setErrorMessage(ex.getMessage());

        return new ResponseEntity(restApiExceptionInfo, restApiExceptionInfo.getHttpStatus());
    }
}