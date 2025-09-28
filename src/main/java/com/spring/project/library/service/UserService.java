package com.spring.project.library.service;

import com.spring.project.library.dto.UserInfosDto;
import com.spring.project.library.dto.UserRegistrationDto;
import com.spring.project.library.dto.UserUpdateRequestDto;
import com.spring.project.library.exception.ResourceNotFoundException;
import com.spring.project.library.model.UserRole;
import com.spring.project.library.exception.UserAlreadyExistsException;
import com.spring.project.library.model.Role;
import com.spring.project.library.model.User;
import com.spring.project.library.repository.RoleRepository;
import com.spring.project.library.repository.UserRepository;
import com.spring.project.library.repository.UserRoleRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       RoleRepository roleRepository, UserRoleRepository userRoleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.userRoleRepository = userRoleRepository;
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    public List<Role> getRolesForUser(Long userId) {
        List<UserRole> userRoles = userRoleRepository.findByUserId(userId);
        return userRoles.stream()
                .map(UserRole::getRole)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User không tồn tại với ID: " + userId);
        }

        // Xóa các bản ghi liên quan trong bảng users_roles trước
        userRoleRepository.deleteByUserId(userId);

        // Xóa User chính
        userRepository.deleteById(userId);
    }

    @Transactional
    public UserInfosDto updateUser(Long userId, UserUpdateRequestDto updateDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User không tồn tại với ID: " + userId));

        User updatedUser = updateBaseUserInfo(user, updateDTO);

        // CẬP NHẬT ROLE (Chỉ chạy nếu có dữ liệu role được gửi lên)
        if (updateDTO.getRoles() != null) {

            // Xóa tất cả các vai trò cũ khỏi bảng users_roles
            userRoleRepository.deleteByUserId(userId);

            // Thêm các vai trò mới
            for (String roleName : updateDTO.getRoles()) {
                String fullRoleName = "ROLE_" + roleName.toUpperCase();

                Role role = roleRepository.findByName(fullRoleName)
                        .orElseThrow(() -> new ResourceNotFoundException("Role không tồn tại: " + fullRoleName));

                // Tạo và lưu bản ghi UserRole mới
                UserRole userRole = new UserRole();
                userRole.setUser(user);
                userRole.setRole(role);
                userRoleRepository.save(userRole);
            }
        }

        return new UserInfosDto(updatedUser, updateDTO.getRoles());
    }

    @Transactional
    public User updateBasicUserInfo(String username, UserUpdateRequestDto updateDTO) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User không tồn tại với username: " + username));

        return updateBaseUserInfo(user, updateDTO);
    }

    private User updateBaseUserInfo(User user, UserUpdateRequestDto updateDTO) {
        Long userId = user.getId();

        // 1. KIỂM TRA TÍNH DUY NHẤT VÀ CẬP NHẬT EMAIL
        final String currentEmail = user.getEmail();
        final String newEmail = updateDTO.getEmail();

        // Nếu email được cung cấp và khác giá trị cũ
        if (newEmail != null && !newEmail.equals(currentEmail)) {
            // Kiểm tra trùng lặp (loại trừ user hiện tại)
            if (userRepository.findByEmailAndIdNot(newEmail, userId).isPresent()) {
                throw new UserAlreadyExistsException("Email '" + newEmail + "' is already taken");
            }
            user.setEmail(newEmail);
        }

        // 2. CẬP NHẬT PASSWORD
        // Nếu password được cung cấp, cập nhật
        if (updateDTO.getPassword() != null && !updateDTO.getPassword().isEmpty()) {
            user.setPassword(updateDTO.getPassword());
        }

        // 3. CẬP NHẬT FULLNAME & PHONE
        if (updateDTO.getFullName() != null) {
            user.setFullName(updateDTO.getFullName());
        }
        if (updateDTO.getPhone() != null) {
            user.setPhone(updateDTO.getPhone());
        }

        // Lưu User Entity đã cập nhật
        return userRepository.save(user);
    }

    public User registerNewUser(UserRegistrationDto registrationDto) {
        // 1. Kiểm tra xem người dùng đã tồn tại chưa
        if (userRepository.findByUsername(registrationDto.getUsername()).isPresent()) {
            throw new UserAlreadyExistsException("Username is already taken: " + registrationDto.getUsername());
        }
        if (userRepository.findByEmail(registrationDto.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("Email is already taken: " + registrationDto.getEmail());
        }

        // 2. Ánh xạ DTO sang Entity User
        User user = new User();
        user.setUsername(registrationDto.getUsername());
        user.setFullName(registrationDto.getFullName());
        user.setEmail(registrationDto.getEmail());
        user.setPhone(registrationDto.getPhone());
        user.setPassword(registrationDto.getPassword());
//        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        user.setEnabled(true); // Kích hoạt tài khoản mặc định

        // Lưu người dùng vào cơ sở dữ liệu
        User savedUser = userRepository.save(user);

        // 3. Xử lý và lưu Role vào bảng users_roles

        List<String> prefixedRoleNames = registrationDto.getPrefixedRoles();

        if (prefixedRoleNames.isEmpty()) {
            throw new IllegalArgumentException("Người dùng phải có ít nhất một Role.");
        }

        for (String roleName : prefixedRoleNames) {
            // 3a. Tìm Role Entity trong DB
            Role role = roleRepository.findByName(roleName)
                    .orElseThrow(() -> new IllegalArgumentException("Role '" + roleName + "' không tồn tại."));

            // 3b. Tạo Entity bảng nối UserRole
            UserRole userRole = new UserRole();
            userRole.setUser(savedUser);
            userRole.setRole(role);

            // 3c. Lưu Entity UserRole vào bảng users_roles
            userRoleRepository.save(userRole);
        }

        return savedUser;
    }

    @Transactional(readOnly = true) // Đánh dấu chỉ đọc
    public List<UserInfosDto> getAllUsersWithRoles() {
        // 1. Lấy tất cả User
        List<User> users = userRepository.findAll();

        // 2. Chuyển đổi sang DTO và gán Roles
        return users.stream()
                .map(user -> {
                    // Tái sử dụng logic lấy Roles từ UserRoleRepository
                    List<String> roleNames = userRoleRepository.findByUserId(user.getId())
                            .stream()
                            .map(userRole -> userRole.getRole().getName())
                            .collect(Collectors.toList());

                    return new UserInfosDto(user, roleNames);
                })
                .collect(Collectors.toList());
    }
    public UserInfosDto getUserByIdWithRoles(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User không tồn tại với ID: " + userId));

        // Lấy roles giống như trong getAllUsersWithRoles()
        List<String> roleNames = userRoleRepository.findByUserId(user.getId())
                .stream()
                .map(userRole -> userRole.getRole().getName())
                .collect(Collectors.toList());

        return new UserInfosDto(user, roleNames);
    }
}