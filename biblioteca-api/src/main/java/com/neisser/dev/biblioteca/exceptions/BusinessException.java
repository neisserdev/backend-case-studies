package com.neisser.dev.biblioteca.exceptions;

import java.util.Map;

public abstract class BusinessException extends RuntimeException {

    private final transient ErrorCode errorCode;

    protected BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    protected BusinessException(ErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public Map<String, Object> getProperties() {
        return Map.of();
    }
}
