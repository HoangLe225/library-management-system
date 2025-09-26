package com.spring.project.library.dto;

import com.spring.project.library.model.User;
import lombok.Data;

import java.util.List;

@Data
public class UserInfosDto {
    private Long id;
    private String username;
    private String password;
    private String fullName;
    private String email;
    private String phone;
    private List<String> roles;

    // Constructor để ánh xạ từ User Entity và List<Role Name>
    public UserInfosDto(User user, List<String> roles) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.fullName = user.getFullName();
        this.email = user.getEmail();
        this.phone = user.getPhone();
        this.roles = roles;
    }
}