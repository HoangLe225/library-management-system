package com.spring.project.library.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

@Configuration
@EnableWebSecurity
public class SecurityConfigAPI {

    // Tạo PasswordEncoder để mã hóa mật khẩu
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        // Trả về NoOpPasswordEncoder.getInstance() để sử dụng mật khẩu DẠNG PLAIN TEXT
        return NoOpPasswordEncoder.getInstance();
    }

    // THÊM BEAN CẤU HÌNH CORS
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // **QUAN TRỌNG:** Origin của Frontend của bạn
        // http://127.0.0.1:5500 là địa chỉ phổ biến của Live Server VS Code
        configuration.setAllowedOrigins(Arrays.asList("http://127.0.0.1:5500", "http://localhost:5500", "http://localhost:3000"));

        // Cho phép các method được sử dụng (GET, POST, OPTIONS,...)
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // Cho phép tất cả các header được gửi
        configuration.setAllowedHeaders(Collections.singletonList("*"));

        // Cho phép gửi các thông tin xác thực như cookies hoặc header Authorization
        configuration.setAllowCredentials(true);

        // Đăng ký cấu hình CORS cho tất cả các đường dẫn (/**)
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    // Cấu hình SecurityFilterChain chính
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                // Vô hiệu hóa CSRF vì REST API thường không dùng session (Stateless)
                .csrf(csrf -> csrf.disable())

                // Cấu hình phân quyền cho các request
                .authorizeHttpRequests(authorize -> authorize
                        // Cho phép tất cả các request tới /login và /api/public (ví dụ)
                        .requestMatchers("/login", "/logout", "/register", "/api/public/**").permitAll()
                        // Yêu cầu quyền ADMIN cho các request tới /api/admin
                        .requestMatchers("/users/**", "/loans/**").hasAnyRole("MEMBER","ADMIN")
                        // Yêu cầu xác thực cho tất cả các request còn lại
                        .anyRequest().authenticated()
                )

                // Cấu hình Basic Authentication
                .httpBasic(Customizer.withDefaults())

                // Cấu hình Session Management: Đặt là STATELESS cho REST API
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}