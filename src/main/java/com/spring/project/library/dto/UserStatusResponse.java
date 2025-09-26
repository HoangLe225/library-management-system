package com.spring.project.library.dto;

import java.util.List;

public class UserStatusResponse {
    private final String status;
    private final String message;
    private final List<String> roles;

    // Nếu có JWT, bạn sẽ thêm private String token; ở đây

    public UserStatusResponse(String status, String message, List<String> roles) {
        this.status = status;
        this.message = message;
        this.roles = roles;
    }

    // Getters
    public String getStatus() { return status; }
    public String getMessage() { return message; }
    public List<String> getRoles() { return roles; }
}
