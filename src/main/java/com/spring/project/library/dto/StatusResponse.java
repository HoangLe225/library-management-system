package com.spring.project.library.dto;

// Dùng cho response thành công. Hiện tại chưa dùng token.

public class StatusResponse {
    private final String status;
    private final String message;

    // Nếu có JWT, bạn sẽ thêm private String token; ở đây

    public StatusResponse(String status, String message) {
        this.status = status;
        this.message = message;
    }

    // Getters
    public String getStatus() { return status; }
    public String getMessage() { return message; }
}