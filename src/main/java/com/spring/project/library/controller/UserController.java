package com.spring.project.library.controller;

import com.spring.project.library.dto.UserInfosDto;
import com.spring.project.library.dto.UserUpdateRequestDto;
import com.spring.project.library.model.Role;
import com.spring.project.library.model.User;
import com.spring.project.library.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
    }

//    private Long getCurrentUserId(Authentication authentication) {
//        String username = authentication.getName();
//        User currentUser = userService.findByUsername(username);
//        return currentUser.getId();
//    }

    // GET all users
//    @GetMapping
//    public ResponseEntity<List<User>> getAllUsers() {
//        List<User> users = userService.getAllUsers();
//        return ResponseEntity.ok(users); // HTTP 200
//    }

    // GET user by ID
//    @GetMapping("/{id}")
//    public ResponseEntity<User> getUserById(@PathVariable Long id) {
//        return userService.getUserById(id)
//                .map(ResponseEntity::ok) // HTTP 200
//                .orElseGet(() -> ResponseEntity.notFound().build()); // HTTP 404
//    }

    // POST create user
//    @PostMapping
//    public ResponseEntity<User> createUser(@RequestBody User user) {
//        User createdUser = userService.saveUser(user);
//        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser); // HTTP 201
//    }

    @GetMapping
    public List<UserInfosDto> getAllUsersWithRoles() {
        return userService.getAllUsersWithRoles();
    }

    @GetMapping("/{id}")
    public UserInfosDto getUserByIdWithRoles(@PathVariable Long id) {
        return userService.getUserByIdWithRoles(id);
    }

    // PUT update user
    @PutMapping("/{id}")
    public UserInfosDto updateUser(@PathVariable Long id,
                                        @RequestBody UserUpdateRequestDto updateDTO) {
        return userService.updateUser(id, updateDTO);
    }

    // DELETE user
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/roles")
    public List<Role> getRolesForUser(@PathVariable Long id) {
        return userService.getRolesForUser(id);
    }

    /**
     * Endpoint này được gọi ngay sau khi đăng nhập thành công (Basic Auth)
     * để lấy thông tin chi tiết của người dùng đã xác thực.
     * * @param authentication Đối tượng chứa thông tin người dùng từ Security Context.
     * @return UserInfosDto chứa ID, username và roles.
     */
    @GetMapping("/user-details")
    public UserInfosDto getUserDetails(Authentication authentication) {

        // 1. Lấy username từ đối tượng đã xác thực
        String username = authentication.getName();

        // 2. Tìm đối tượng User đầy đủ từ Service
        User user = userService.findByUsername(username);

        // 3. Xây dựng DTO phản hồi (LoginResponseDto)
        // 3.1. Ánh xạ Roles
        List<String> roles = user.getUserRoles().stream() // Giả sử user.getUserRoles() đã hoạt động
                .map(ur -> ur.getRole().getName())
                .collect(Collectors.toList());

        // 3.2. Tạo đối tượng UserInfosDto
        return new UserInfosDto(user, roles);
    }

    @PutMapping("/user-details")
    public User updateUserDetails(Authentication authentication, @RequestBody UserUpdateRequestDto updateDTO) {
        String username = authentication.getName();
        return userService.updateBasicUserInfo(username, updateDTO);
    }
}
