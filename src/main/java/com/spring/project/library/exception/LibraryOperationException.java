package com.spring.project.library.exception;

public class LibraryOperationException extends RuntimeException {
    public LibraryOperationException (String message) {
        super(message);
    }
}
