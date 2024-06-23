package com.project.watermelon.exception;

public class SeatAlreadyReservedException extends CustomException {
    public SeatAlreadyReservedException(String message) {
        super(ErrorCode.BAD_REQUEST, message);
    }
}
