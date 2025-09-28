package com.spring.project.library.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex,
            WebRequest request) {

        List<FieldErrorDetail> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> new FieldErrorDetail(error.getField(), error.getDefaultMessage()))
                .collect(Collectors.toList());

        ValidationErrorResponse response = new ValidationErrorResponse(
                ZonedDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "Validation Failed",
                request.getDescription(false).replace("uri=", ""),
                "INPUT_VALIDATION_ERROR",
                fieldErrors
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InputValidationException.class)
    public ResponseEntity<ValidationErrorResponse> handleCustomValidationException(
            InputValidationException ex,
            WebRequest request) {

        ValidationErrorResponse response = new ValidationErrorResponse(
                ZonedDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                ex.getMessage(),
                request.getDescription(false).replace("uri=", ""),
                ex.getErrorCode(),
                ex.getErrors()
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // Username not found (Spring Security)
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ValidationErrorResponse> handleUsernameNotFound(
            UsernameNotFoundException ex, WebRequest request) {

        ValidationErrorResponse response = new ValidationErrorResponse(
                ZonedDateTime.now(),
                HttpStatus.UNAUTHORIZED.value(),
                "Unauthorized",
                ex.getMessage(),
                request.getDescription(false).replace("uri=", ""),
                "USERNAME_NOT_FOUND",
                null
        );

        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    // IllegalArgument
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ValidationErrorResponse> handleIllegalArgument(
            IllegalArgumentException ex, WebRequest request) {

        ValidationErrorResponse response = new ValidationErrorResponse(
                ZonedDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                ex.getMessage(),
                request.getDescription(false).replace("uri=", ""),
                "ILLEGAL_ARGUMENT",
                null
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ValidationErrorResponse> handleResourceNotFound(
            ResourceNotFoundException ex, WebRequest request) {

        ValidationErrorResponse response = new ValidationErrorResponse(
                ZonedDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                ex.getMessage(),
                request.getDescription(false).replace("uri=", ""),
                "RESOURCE_NOT_FOUND",
                null
        );

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ValidationErrorResponse> handleResourceConflict(
            UserAlreadyExistsException ex, WebRequest request) {

        ValidationErrorResponse response = new ValidationErrorResponse(
                ZonedDateTime.now(),
                HttpStatus.CONFLICT.value(),
                "Conflict",
                "Data validation conflict occurred.",
                request.getDescription(false).replace("uri=", ""),
                "RESOURCE_CONFLICT",
                null
        );

        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ValidationErrorResponse> handleGenericException(
            Exception ex, WebRequest request) {

        ValidationErrorResponse response = new ValidationErrorResponse(
                ZonedDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                ex.getMessage(),
                request.getDescription(false).replace("uri=", ""),
                "INTERNAL_ERROR",
                null
        );

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}

