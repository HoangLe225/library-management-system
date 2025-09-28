package com.spring.project.library.exception;

import lombok.Data;

@Data
public class FieldErrorDetail {
    private String field;
    private String message;

    public FieldErrorDetail(String field, String message) {
        this.field = field;
        this.message = message;
    }

    // getters/setters omitted for brevity
}

