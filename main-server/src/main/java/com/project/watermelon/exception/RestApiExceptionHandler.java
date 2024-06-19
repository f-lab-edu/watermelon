package com.project.watermelon.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RestApiExceptionHandler {

    @ExceptionHandler(value = { IllegalArgumentException.class, NullPointerException.class })
    public ResponseEntity<Object> handleCommonExceptions(RuntimeException ex) {
        ErrorCode errorCode = ErrorCode.BAD_REQUEST;
        if (ex instanceof CustomException) {
            errorCode = ((CustomException) ex).getErrorCode();
        }

        RestApiExceptionInfo restApiExceptionInfo = new RestApiExceptionInfo();
        restApiExceptionInfo.setHttpStatus(errorCode.getHttpStatus());
        restApiExceptionInfo.setErrorMessage(ex.getMessage());
        restApiExceptionInfo.setErrorCode(errorCode.getErrorCode());

        return new ResponseEntity<>(restApiExceptionInfo, restApiExceptionInfo.getHttpStatus());
    }

    private ResponseEntity<Object> getObjectResponseEntity(CustomException ex) {
        RestApiExceptionInfo restApiExceptionInfo = new RestApiExceptionInfo();
        ErrorCode errorCode = ex.getErrorCode();

        restApiExceptionInfo.setHttpStatus(errorCode.getHttpStatus());
        restApiExceptionInfo.setErrorMessage(ex.getMessage());
        restApiExceptionInfo.setErrorCode(errorCode.getErrorCode());

        return new ResponseEntity<>(restApiExceptionInfo, restApiExceptionInfo.getHttpStatus());
    }

    @ExceptionHandler(value = { MemberAlreadyRequestReservationException.class })
    public ResponseEntity<Object> handleMemberAlreadyRequestReservationExceptions(CustomException ex) {
        return getObjectResponseEntity(ex);
    }

    @ExceptionHandler(value = { MemberIsNotOwnerOfReservationException.class })
    public ResponseEntity<Object> handleMemberIsNotOwnerOfReservationExceptions(CustomException ex) {
        return getObjectResponseEntity(ex);
    }

    @ExceptionHandler(value = { SeatAlreadyReservedException.class })
    public ResponseEntity<Object> handleSeatAlreadyReservedExceptions(CustomException ex) {
        return getObjectResponseEntity(ex);
    }

    @ExceptionHandler(value = { NotAvailableStatusException.class })
    public ResponseEntity<Object> handleNotAvailableStatusExceptions(CustomException ex) {
        return getObjectResponseEntity(ex);
    }

    @ExceptionHandler(value = { InvalidIdException.class })
    public ResponseEntity<Object> handleInvalidIdExceptions(CustomException ex) {
        return getObjectResponseEntity(ex);
    }
}
