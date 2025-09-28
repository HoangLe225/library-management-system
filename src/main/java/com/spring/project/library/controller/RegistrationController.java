package com.spring.project.library.controller;

import com.spring.project.library.dto.UserRegistrationDto;
import com.spring.project.library.model.User;
import com.spring.project.library.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RegistrationController {
    private final UserService userService;

    public RegistrationController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> createUser(@Valid @RequestBody UserRegistrationDto registrationDto) {
        User createdUser = userService.registerNewUser(registrationDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }
}
