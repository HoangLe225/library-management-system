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
@RequestMapping("/users")
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
    public ResponseEntity<List<UserInfosDto>> getAllUsersWithRoles() {
        List<UserInfosDto> usersWithRoles = userService.getAllUsersWithRoles();
        return ResponseEntity.ok(usersWithRoles); // HTTP 200 OK
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserInfosDto> getUserByIdWithRoles(@PathVariable Long id) {
        // Giả sử userService có phương thức getUserByIdWithRoles
        UserInfosDto userDTO = userService.getUserByIdWithRoles(id);
        return ResponseEntity.ok(userDTO);
    }

    // PUT update user
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id,
                                        @RequestBody UserUpdateRequestDto updateDTO) {
        UserInfosDto userInfos = userService.updateUser(id, updateDTO);
        return ResponseEntity.ok(userInfos);
    }

    // DELETE user
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/roles")
    public ResponseEntity<List<Role>> getRolesForUser(@PathVariable Long id) {
        Optional<User> userOptional = userService.getUserById(id);
        if (userOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        List<Role> roles = userService.getRolesForUser(id);
        return ResponseEntity.ok(roles);
    }

    /**
     * Endpoint này được gọi ngay sau khi đăng nhập thành công (Basic Auth)
     * để lấy thông tin chi tiết của người dùng đã xác thực.
     * * @param authentication Đối tượng chứa thông tin người dùng từ Security Context.
     * @return UserInfosDto chứa ID, username và roles.
     */
    @GetMapping("/user-details")
    public ResponseEntity<UserInfosDto> getUserDetails(Authentication authentication) {

        // 1. Lấy username từ đối tượng đã xác thực
        String username = authentication.getName();

        // 2. Tìm đối tượng User đầy đủ từ Service
        // Phương thức này sẽ ném ra UsernameNotFoundException nếu không tìm thấy (lý thuyết không xảy ra)
        User user = userService.findByUsername(username);

        // 3. Xây dựng DTO phản hồi (LoginResponseDto)
        // 3.1. Ánh xạ Roles
        List<String> roles = user.getUserRoles().stream() // Giả sử user.getUserRoles() đã hoạt động
                .map(ur -> ur.getRole().getName())
                .collect(Collectors.toList());

        // 3.2. Tạo đối tượng UserInfosDto
        // Lưu ý: DTO của bạn dùng List<String> roles nên ta dùng toList() thay vì toSet()
        UserInfosDto responseDto = new UserInfosDto(user, roles);

        return ResponseEntity.ok(responseDto);
    }

    @PutMapping("/user-details")
    public ResponseEntity<?> updateUserDetails(Authentication authentication, @RequestBody UserUpdateRequestDto updateDTO) {
        String username = authentication.getName();

        // Phương thức này BỎ QUA field 'roles' trong DTO.
        User updatedUser = userService.updateBasicUserInfo(username, updateDTO);

        return ResponseEntity.ok(updatedUser);
    }
}
