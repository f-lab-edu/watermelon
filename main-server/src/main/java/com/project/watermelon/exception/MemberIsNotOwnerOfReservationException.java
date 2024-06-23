package com.project.watermelon.exception;

public class MemberIsNotOwnerOfReservationException extends CustomException {
    public MemberIsNotOwnerOfReservationException(String message) {
        super(ErrorCode.BAD_REQUEST, message);
    }
}
