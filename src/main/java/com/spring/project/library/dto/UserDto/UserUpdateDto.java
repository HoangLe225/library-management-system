package com.spring.project.library.dto.UserDto;

import lombok.Data;

import java.util.List;

@Data
public class UserUpdateDto {
    private String password;
    private String fullName;
    private String email;
    private String phone;
    private List<String> roles;
}
