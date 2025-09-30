package com.spring.project.library.controller;

import com.spring.project.library.dto.LoginDto;
import com.spring.project.library.dto.UserRegistrationDto;
import com.spring.project.library.dto.UserStatusResponse;
import com.spring.project.library.model.User;
import com.spring.project.library.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    // Tiêm AuthenticationManager vào Controller
    private final AuthenticationManager authenticationManager;
    private final UserService userService;

    public AuthController(AuthenticationManager authenticationManager, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginDto loginRequest) {
        // 1. Thực hiện xác thực
        Authentication authenticationToken =
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword());

        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        // 2. Lấy UserDetails và Vai trò (Roles)
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // Ánh xạ GrantedAuthority sang List<String>
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        // 2. Xác thực thành công: Trả về 200 OK với JSON body
        // Frontend của bạn mong đợi một JSON body, kể cả khi không có token.
        UserStatusResponse userStatusResponse = new UserStatusResponse("SUCCESS", "Đăng nhập thành công.", roles);

        // Bạn có thể dùng Http Session ở đây nếu cần, nhưng thường REST API là STATELESS
        // SecurityContextHolder.getContext().setAuthentication(authentication);

        return ResponseEntity.ok(userStatusResponse);
    }

    @PostMapping("/register")
    public ResponseEntity<?> createUser(@Valid @RequestBody UserRegistrationDto registrationDto) {
        User createdUser = userService.registerNewUser(registrationDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        // Logic xử lý Logout trên Server:
        // 1. Nếu dùng JWT, đây là nơi bạn có thể thêm JWT vào Blacklist (nếu cần).
        // 2. Xóa bất kỳ thông tin xác thực nào có liên quan đến Refresh Token (nếu có).

        // Với Basic Auth/Stateless: Server không giữ trạng thái, nên chỉ cần trả về thành công.
        return ResponseEntity.noContent().build();
    }
}