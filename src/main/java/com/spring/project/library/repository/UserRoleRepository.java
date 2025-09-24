package com.spring.project.library.repository;

import com.spring.project.library.dto.UserRole;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
    @EntityGraph(attributePaths = {"user", "role"})
    List<UserRole> findByUserId(Long userId);
}
