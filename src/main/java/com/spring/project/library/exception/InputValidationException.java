package com.spring.project.library.exception;

import java.util.List;

public class InputValidationException extends RuntimeException {
    private List<FieldErrorDetail> errors;
    private String errorCode;

    public InputValidationException(String message, String errorCode, List<FieldErrorDetail> errors) {
        super(message);
        this.errorCode = errorCode;
        this.errors = errors;
    }

    public List<FieldErrorDetail> getErrors() {
        return errors;
    }

    public String getErrorCode() {
        return errorCode;
    }
}

