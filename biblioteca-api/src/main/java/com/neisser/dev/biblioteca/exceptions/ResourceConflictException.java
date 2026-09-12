package com.neisser.dev.biblioteca.exceptions;

public class ResourceConflictException extends BusinessException {

    public ResourceConflictException(String message) {
        super(ErrorCode.RESOURCE_CONFLICT, message);
    }
}
