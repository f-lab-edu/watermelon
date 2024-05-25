package com.project.watermelon.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class MemberAlreadyRequestReservationException extends RuntimeException {
    public MemberAlreadyRequestReservationException(String message) {
        super(message);
    }
}
