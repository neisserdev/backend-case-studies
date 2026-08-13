package com.neisser.dev.hospedium_api.exceptions;

public class ResourceConflictException extends BusinessException {

    public ResourceConflictException(String message) {
        super(ErrorCode.RESOURCE_CONFLICT, message);
    }
}
