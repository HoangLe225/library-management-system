package com.spring.project.library.exception;

import lombok.Data;

import java.time.ZonedDateTime;
import java.util.List;

@Data
public class ValidationErrorResponse {
    private ZonedDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
    private String errorCode;
    private List<FieldErrorDetail> details;

    // Getters, Setters, Constructors

    public ValidationErrorResponse(ZonedDateTime timestamp, int status, String error, String message,
                                   String path, String errorCode, List<FieldErrorDetail> details) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
        this.errorCode = errorCode;
        this.details = details;
    }

    // getters/setters omitted for brevity
}
