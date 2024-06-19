package com.project.watermelon.exception;

public class NotAvailableStatusException extends CustomException {
    public NotAvailableStatusException(String message) {
        super(ErrorCode.BAD_REQUEST, message);
    }
}
