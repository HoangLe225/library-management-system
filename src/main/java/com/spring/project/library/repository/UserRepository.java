package com.spring.project.library.repository;

import com.spring.project.library.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<Object> findByEmail(String email);
    // Tìm người dùng theo email, loại trừ ID hiện tại
    Optional<User> findByEmailAndIdNot(String email, Long id);
}
