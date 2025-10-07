package com.spring.project.library.service;

import com.spring.project.library.dto.UserDto.UserCreationDto;
import com.spring.project.library.dto.UserDto.UserResponseDto;
import com.spring.project.library.dto.UserDto.UserUpdateDto;
import com.spring.project.library.exception.ResourceNotFoundException;
import com.spring.project.library.exception.UserAlreadyExistsException;
import com.spring.project.library.mapper.UserMapper;
import com.spring.project.library.model.Role;
import com.spring.project.library.model.User;
import com.spring.project.library.model.UserRole;
import com.spring.project.library.repository.RoleRepository;
import com.spring.project.library.repository.UserRepository;
import com.spring.project.library.repository.UserRoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// Kích hoạt Mockito cho JUnit 5
@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Unit Tests")
public class UserServiceTest {

    // Dependencies được Mock
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserRoleRepository userRoleRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserMapper userMapper;

    // Đối tượng cần kiểm thử
    @InjectMocks
    private UserService userService;

    // Các đối tượng giả lập
    private final Long USER_ID = 1L;
    private User testUser;
    private Role userRoleEntity;
    private UserResponseDto testUserResponseDto;
    private UserCreationDto testCreationDto;

    @BeforeEach
    void setUp() {
        // --- 1. KHỞI TẠO USER ENTITY GIẢ ---
        testUser = new User();
        testUser.setId(USER_ID);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("encoded_password");
        testUser.setFullName("Test User");
        testUser.setPhone("1234567890");
        testUser.setEnabled(true);
        testUser.setCreatedAt(LocalDateTime.now());
        testUser.setUpdatedAt(LocalDateTime.now());

        // --- 2. KHỞI TẠO ROLE ENTITY GIẢ ---
        userRoleEntity = new Role();
        userRoleEntity.setId(101L);
        userRoleEntity.setName("ROLE_MEMBER");

        // --- 3. KHỞI TẠO USER RESPONSE DTO GIẢ ---
        testUserResponseDto = new UserResponseDto();
        testUserResponseDto.setId(USER_ID);
        testUserResponseDto.setUsername("testuser");
        testUserResponseDto.setEmail("test@example.com");
        testUserResponseDto.setFullName("Test User");
        testUserResponseDto.setRoles(List.of("ROLE_MEMBER"));

        // --- 4. KHỞI TẠO USER CREATION DTO GIẢ ---
        testCreationDto = new UserCreationDto();
        testCreationDto.setUsername("newuser");
        testCreationDto.setEmail("new@example.com");
        testCreationDto.setPassword("newpass");
        // setRoles: Chỉ chứa tên role thô, không có prefix (ví dụ: "ADMIN")
        testCreationDto.setRoles(List.of("MEMBER"));
    }

    // ====================================================================
    // 1. KIỂM THỬ findByUsername
    // ====================================================================

    @Test
    @DisplayName("findByUsername: Should return DTO when user exists")
    void findByUsername_UserExists_ShouldReturnDto() {
        // GIVEN
        UserRole userRole = new UserRole(10L, testUser, userRoleEntity);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(userRoleRepository.findByUserId(USER_ID)).thenReturn(List.of(userRole));
        when(userMapper.toResponseDto(testUser)).thenReturn(testUserResponseDto);

        // WHEN
        UserResponseDto result = userService.findByUsername("testuser");

        // THEN
        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
        assertTrue(result.getRoles().contains("ROLE_MEMBER"));
        verify(userRepository, times(1)).findByUsername("testuser");
    }

    @Test
    @DisplayName("findByUsername: Should throw UsernameNotFoundException when user does not exist")
    void findByUsername_UserNotFound_ShouldThrowException() {
        // GIVEN
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // WHEN & THEN
        assertThrows(UsernameNotFoundException.class, () -> {
            userService.findByUsername("nonexistent");
        });
        verify(userRepository, times(1)).findByUsername("nonexistent");
        verifyNoInteractions(userRoleRepository, userMapper);
    }

    // ====================================================================
    // 2. KIỂM THỬ deleteUser
    // ====================================================================

    @Test
    @DisplayName("deleteUser: Should delete user successfully")
    void deleteUser_UserExists_ShouldDelete() {
        // GIVEN
        when(userRepository.existsById(USER_ID)).thenReturn(true);

        // WHEN
        userService.deleteUser(USER_ID);

        // THEN
        verify(userRoleRepository, times(1)).deleteByUserId(USER_ID);
        verify(userRepository, times(1)).deleteById(USER_ID);
    }

    @Test
    @DisplayName("deleteUser: Should throw ResourceNotFoundException when user not found")
    void deleteUser_UserNotFound_ShouldThrowException() {
        // GIVEN
        when(userRepository.existsById(USER_ID)).thenReturn(false);

        // WHEN & THEN
        assertThrows(ResourceNotFoundException.class, () -> {
            userService.deleteUser(USER_ID);
        });

        // Xác minh không có thao tác xóa nào được thực hiện
        verify(userRoleRepository, never()).deleteByUserId(anyLong());
        verify(userRepository, never()).deleteById(anyLong());
    }

    // ====================================================================
    // 3. KIỂM THỬ registerNewUser
    // ====================================================================

    @Test
    @DisplayName("registerNewUser: Should register and assign roles successfully")
    void registerNewUser_ValidData_ShouldSucceed() {
        // GIVEN
        // Thiết lập User Entity sẽ được trả về sau khi lưu
        User savedUser = new User();
        savedUser.setId(2L);
        savedUser.setUsername(testCreationDto.getUsername());
        savedUser.setEmail(testCreationDto.getEmail());

        // Tạo UserResponseDto giả lập ĐÚNG với ID của savedUser
        UserResponseDto expectedResponseDto = new UserResponseDto();
        expectedResponseDto.setId(2L); // ID phải là 2L
        // Cần setup các trường khác để convertUserToDto không lỗi
        expectedResponseDto.setUsername(savedUser.getUsername());
        expectedResponseDto.setRoles(List.of("ROLE_MEMBER"));

        // Mocking kiểm tra tồn tại (trả về empty)
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // Mocking ánh xạ và lưu User
        when(userMapper.toEntity(any(UserCreationDto.class))).thenReturn(new User());
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // Mocking Role finding
        when(roleRepository.findByName("ROLE_MEMBER")).thenReturn(Optional.of(userRoleEntity));

        // Mocking convertUserToDto
        UserRole userRole = new UserRole(10L, savedUser, userRoleEntity);
        when(userRoleRepository.findByUserId(savedUser.getId())).thenReturn(List.of(userRole));
        when(userMapper.toResponseDto(savedUser)).thenReturn(expectedResponseDto);


        // WHEN
        UserResponseDto result = userService.registerNewUser(testCreationDto);

        // THEN
        assertNotNull(result);
        assertEquals(2L, result.getId());

        // Xác minh các thao tác chính
        verify(userRepository, times(1)).save(any(User.class));
        verify(roleRepository, times(1)).findByName("ROLE_MEMBER");
        verify(userRoleRepository, times(1)).save(any(UserRole.class));
    }

    @Test
    @DisplayName("registerNewUser: Should throw UserAlreadyExistsException if username is taken")
    void registerNewUser_UsernameExists_ShouldThrowException() {
        // GIVEN
        when(userRepository.findByUsername(testCreationDto.getUsername())).thenReturn(Optional.of(testUser));

        // WHEN & THEN
        assertThrows(UserAlreadyExistsException.class, () -> {
            userService.registerNewUser(testCreationDto);
        });

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("registerNewUser: Should throw IllegalArgumentException if role does not exist")
    void registerNewUser_RoleNotFound_ShouldThrowException() {
        // GIVEN
        testCreationDto.setRoles(List.of("NONEXISTENT_ROLE"));

        // Thiết lập save User thành công trước khi tìm role
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(userMapper.toEntity(any(UserCreationDto.class))).thenReturn(new User());
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Mocking Role finding: Trả về rỗng
        when(roleRepository.findByName("ROLE_NONEXISTENT_ROLE")).thenReturn(Optional.empty());

        // WHEN & THEN
        assertThrows(IllegalArgumentException.class, () -> {
            userService.registerNewUser(testCreationDto);
        }, "Nên ném IllegalArgumentException khi Role không tồn tại");

        // Xác minh User đã được lưu, nhưng UserRole chưa được lưu
        verify(userRepository, times(1)).save(any(User.class));
        verify(userRoleRepository, never()).save(any(UserRole.class));
    }

    // ====================================================================
    // 4. KIỂM THỬ updateUser (Cập nhật thông tin cơ bản & Role)
    // ====================================================================

    @Test
    @DisplayName("updateUser: Should update basic info, delete old roles, and add new roles")
    void updateUser_UpdateAllFields_ShouldSucceed() {
        // GIVEN
        UserUpdateDto updateDTO = new UserUpdateDto();
        updateDTO.setEmail("new@test.com");
        updateDTO.setFullName("New Full Name");
        updateDTO.setRoles(List.of("ADMIN"));

        Role newRoleEntity = new Role();
        newRoleEntity.setId(201L);
        newRoleEntity.setName("ROLE_ADMIN");


        // Mocking findById
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(testUser));

        // Mocking kiểm tra tính duy nhất của Email (Không tìm thấy user nào khác có email mới)
        when(userRepository.findByEmailAndIdNot("new@test.com", USER_ID)).thenReturn(Optional.empty());

        // Mocking tìm Role mới
        when(roleRepository.findByName("ROLE_ADMIN")).thenReturn(Optional.of(newRoleEntity));

        // Mocking save User và DTO conversion
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(userMapper.toResponseDto(any(User.class))).thenReturn(testUserResponseDto);
        // Mocking convertUserToDto ở cuối
        when(userRoleRepository.findByUserId(USER_ID)).thenReturn(Collections.emptyList());

        // WHEN
        userService.updateUser(USER_ID, updateDTO);

        // THEN
        // Xác minh thao tác Role: 1. Xóa cũ
        verify(userRoleRepository, times(1)).deleteByUserId(USER_ID);
        // Xác minh thao tác Role: 2. Tìm mới và Lưu mới
        verify(roleRepository, times(1)).findByName("ROLE_ADMIN");
        verify(userRoleRepository, times(1)).save(any(UserRole.class));

        // Xác minh User được lưu
        verify(userRepository, times(1)).save(testUser);

        // Xác minh các setter đã được gọi trên đối tượng testUser
        assertEquals("new@test.com", testUser.getEmail());
        assertEquals("New Full Name", testUser.getFullName());
    }

    @Test
    @DisplayName("updateUser: Should throw UserAlreadyExistsException if new email is taken")
    void updateUser_NewEmailTaken_ShouldThrowException() {
        // GIVEN
        UserUpdateDto updateDTO = new UserUpdateDto();
        updateDTO.setEmail("existing@email.com");

        User anotherUser = new User();

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(testUser));
        // Email mới đã được người dùng khác sử dụng
        when(userRepository.findByEmailAndIdNot("existing@email.com", USER_ID)).thenReturn(Optional.of(anotherUser));

        // WHEN & THEN
        assertThrows(UserAlreadyExistsException.class, () -> {
            userService.updateUser(USER_ID, updateDTO);
        });

        // Xác minh không có thao tác lưu nào xảy ra
        verify(userRepository, never()).save(any(User.class));
    }
}