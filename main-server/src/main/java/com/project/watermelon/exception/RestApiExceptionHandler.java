package com.project.watermelon.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RestApiExceptionHandler {

    @ExceptionHandler(value = { IllegalArgumentException.class })
    public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException ex) {
        RestApiExceptionInfo restApiExceptionInfo = new RestApiExceptionInfo();

        ErrorCode errorCode = ErrorCode.ILLEGAL_ARGUMENT_EXCEPTION;

        restApiExceptionInfo.setHttpStatus(errorCode.getHttpStatus());
        restApiExceptionInfo.setErrorMessage(ex.getMessage());

        return new ResponseEntity<>(restApiExceptionInfo, restApiExceptionInfo.getHttpStatus());
    }

    @ExceptionHandler(value = { NullPointerException.class })
    public ResponseEntity<Object> handleNullPointerException(NullPointerException ex) {
        RestApiExceptionInfo restApiExceptionInfo = new RestApiExceptionInfo();

        ErrorCode errorCode = ErrorCode.NULL_POINTER_EXCEPTION;

        restApiExceptionInfo.setHttpStatus(errorCode.getHttpStatus());
        restApiExceptionInfo.setErrorMessage(ex.getMessage());

        return new ResponseEntity<>(restApiExceptionInfo, restApiExceptionInfo.getHttpStatus());
    }

    @ExceptionHandler(value = { MemberAlreadyRequestReservationException.class })
    public ResponseEntity<Object> handleMemberAlreadyRequestReservationException(MemberAlreadyRequestReservationException ex) {
        RestApiExceptionInfo restApiExceptionInfo = new RestApiExceptionInfo();

        ErrorCode errorCode = ErrorCode.MEMBER_ALREADY_REQUEST_RESERVATION_EXCEPTION;

        restApiExceptionInfo.setHttpStatus(errorCode.getHttpStatus());
        restApiExceptionInfo.setErrorMessage(ex.getMessage());

        return new ResponseEntity<>(restApiExceptionInfo, restApiExceptionInfo.getHttpStatus());
    }
}
