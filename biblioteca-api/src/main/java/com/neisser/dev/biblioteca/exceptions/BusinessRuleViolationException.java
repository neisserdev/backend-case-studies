package com.neisser.dev.biblioteca.exceptions;

public class BusinessRuleViolationException extends BusinessException {

    public BusinessRuleViolationException(String message) {
        super(ErrorCode.BUSINESS_RULE_VIOLATION, message);
    }
}
