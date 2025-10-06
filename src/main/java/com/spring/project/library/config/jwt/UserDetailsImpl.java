package com.spring.project.library.config.jwt;

import com.spring.project.library.model.User; // Giả định đường dẫn đến User Entity
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

// Lớp đại diện cho User trong Spring Security, lưu trữ các thông tin cần thiết

@Data
public class UserDetailsImpl implements UserDetails {

    private static final long serialVersionUID = 1L; // Cần thiết cho Serialization

    private final Long id;
    private final String username;
    private final String password;
    private final String email;
    private final Collection<? extends GrantedAuthority> authorities;

    // Constructor private, chỉ dùng phương thức build để tạo
    private UserDetailsImpl(Long id, String username, String password, String email,
                            Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.authorities = authorities;
    }

    // Phương thức tĩnh để chuyển đổi từ User Entity sang UserDetailsImpl
    public static UserDetailsImpl build(User user,  List<GrantedAuthority> authorities) {

        return new UserDetailsImpl(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                user.getEmail(),
                authorities);
    }

    // --- Các phương thức bắt buộc của UserDetails ---
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }

    // Phương thức Equals/HashCode để so sánh người dùng
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserDetailsImpl user = (UserDetailsImpl) o;
        return Objects.equals(id, user.id);
    }
}