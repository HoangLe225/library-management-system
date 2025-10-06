package com.spring.project.library.config.jwt;

import com.spring.project.library.model.User;
import com.spring.project.library.repository.UserRepository;
// Giữ lại UserRoleRepository để tải Roles từ bảng nối
import com.spring.project.library.repository.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Cần thiết

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    // Inject cả hai Repository
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;

    @Autowired
    public UserDetailsServiceImpl(UserRepository userRepository, UserRoleRepository userRoleRepository) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
    }

    @Override
    // Đảm bảo việc tải User và Roles diễn ra trong cùng một Transaction
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // 1. Tìm kiếm người dùng trong Database
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        // 2. TẢI CÁC VAI TRÒ DÙNG USERROLEREPOSITORY (LOGIC CỦA BẠN)
        // Giả định UserRoleRepository.findByUserId trả về danh sách UserRole Entity
        // và UserRole.getRole().getName() trả về chuỗi ROLE_ADMIN, ROLE_MEMBER, v.v.
        List<GrantedAuthority> authorities = userRoleRepository.findByUserId(user.getId())
                .stream()
                // Giả định UserRole Entity của bạn có thể truy cập Role.getName()
                .map(userRole -> new SimpleGrantedAuthority(userRole.getRole().getName()))
                .collect(Collectors.toList());

        // 3. Chuyển đổi User Entity thành UserDetailsImpl
        return UserDetailsImpl.build(user, authorities);
    }
}