package com.spring.project.library.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class UserRegistrationDto {
    @NotBlank(message = "Username cannot be blank")
    private String username;

    @NotBlank(message = "Full name cannot be blank")
    private String fullName;

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password cannot be blank")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String password;

    @Size(min = 10, message = "Phone number must be at least 10 characters long")
    private String phone;

    @NotEmpty(message = "Roles list cannot be empty")
    private List<String> roles;

    /**
     * Helper method to convert raw roles (e.g., "ADMIN") to prefixed roles (e.g., "ROLE_ADMIN").
     * @return List of roles with ROLE_ prefix.
     */
    public List<String> getPrefixedRoles() {
        if (this.roles == null) {
            return List.of();
        }
        return this.roles.stream()
                .map(role -> "ROLE_" + role.toUpperCase())
                .collect(Collectors.toList());
    }
}