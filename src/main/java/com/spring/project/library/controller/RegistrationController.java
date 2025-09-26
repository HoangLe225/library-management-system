package com.spring.project.library.controller;

import com.spring.project.library.dto.StatusResponse;
import com.spring.project.library.dto.UserRegistrationDto;
import com.spring.project.library.exception.UserAlreadyExistsException;
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
        try {
            User createdUser = userService.registerNewUser(registrationDto);
//            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
            return ResponseEntity.status(HttpStatus.CREATED).body(new StatusResponse("SUCCESS", "Đăng ký thành công."));
        } catch (UserAlreadyExistsException e) {
            // Trả về 409 Conflict hoặc 400 Bad Request nếu username đã tồn tại
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new StatusResponse("ERROR", e.getMessage()));
        } catch (Exception e) {
            // Xử lý các lỗi khác
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new StatusResponse("ERROR", "Lỗi server: " + e.getMessage()));
        }
    }
}
