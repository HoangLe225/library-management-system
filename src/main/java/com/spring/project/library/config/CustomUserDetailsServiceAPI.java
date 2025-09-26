package com.spring.project.library.config;

import com.spring.project.library.model.User;
import com.spring.project.library.repository.UserRepository;
import com.spring.project.library.repository.UserRoleRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsServiceAPI implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;

    // Dependency Injection qua constructor
    public CustomUserDetailsServiceAPI(UserRepository userRepository, UserRoleRepository userRoleRepository) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. Tìm kiếm người dùng trong Database
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        // 2. Tải các vai trò của người dùng từ UserRoleRepository
        List<String> roles = userRoleRepository.findByUserId(user.getId())
                .stream()
                .map(userRole -> userRole.getRole().getName())
                .collect(Collectors.toList());

        // 3. Chuyển đổi List<String> thành List<GrantedAuthority>
        List<GrantedAuthority> authorities = roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        // 4. Chuyển đổi User Entity thành UserDetails sử dụng .authorities()
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                // Sử dụng .authorities() để truyền trực tiếp các đối tượng GrantedAuthority
                .authorities(authorities)
                .build();
    }
}