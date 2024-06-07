package com.project.watermelon.exception;

public class MemberAlreadyRequestReservationException extends CustomException {
    public MemberAlreadyRequestReservationException(String message) {
        super(ErrorCode.BAD_REQUEST, message);
    }
}
