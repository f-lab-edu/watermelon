package com.project.watermelon.exception;

public class InvalidIdException extends CustomException {
    public InvalidIdException(String message) {
        super(ErrorCode.BAD_REQUEST, message);
    }
}
