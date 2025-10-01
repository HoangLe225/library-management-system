package com.spring.project.library.dto.UserDto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class UserResponseDto {
    private Long id;
    private String username;
    private String password;
    private String fullName;
    private String email;
    private String phone;
    private boolean enabled;
    private List<String> roles;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}