package com.project.watermelon.exception;

public class RedisLockException extends CustomException {
    public RedisLockException(String message) {
        super(ErrorCode.BAD_REQUEST, message);
    }
}
