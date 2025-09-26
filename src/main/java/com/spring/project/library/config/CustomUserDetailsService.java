//package com.spring.project.library.config;
//
//import com.spring.project.library.model.User;
//import com.spring.project.library.repository.UserRepository;
//import com.spring.project.library.repository.UserRoleRepository;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.Set;
//import java.util.stream.Collectors;
//
//@Service
//public class CustomUserDetailsService implements UserDetailsService {
//
//    private final UserRepository userRepository;
//
//    private final UserRoleRepository userRoleRepository;
//
//    public CustomUserDetailsService(UserRepository userRepository, UserRoleRepository userRoleRepository) {
//        this.userRepository = userRepository;
//        this.userRoleRepository = userRoleRepository;
//    }
//
//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        try {
//            User user = userRepository
//                    .findByUsername(username)
//                    .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
//
//            System.out.println("User found: " + user.getUsername());
//            System.out.println("User is enabled: " + user.isEnabled());
//
//            // Tải các vai trò của người dùng từ UserRoleRepository
//            List<String> roles = userRoleRepository.findByUserId(user.getId())
//                    .stream()
//                    .map(userRole -> userRole.getRole().getName())
//                    .collect(Collectors.toList());
//
//            // Chuyển đổi các vai trò thành GrantedAuthority
//            Set<GrantedAuthority> authorities = roles.stream()
//                    .map(SimpleGrantedAuthority::new)
//                    .collect(Collectors.toSet());
//
//            return new org.springframework.security.core.userdetails.User(
//                    user.getUsername(),
//                    user.getPassword(),
//                    user.isEnabled(),
//                    true,
//                    true,
//                    true,
//                    authorities
//            );
//
//        } catch (Exception e) {
//            // Ghi log chi tiết để debug
//            System.err.println("❌ Error in loadUserByUsername: " + e.getClass().getName());
//            e.printStackTrace(); // <--- in full stack trace ra console
//            throw e;
//        }
//    }
//}