package com.spring.project.library.service;

import com.spring.project.library.repository.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    // Lấy tất cả tên role (ví dụ: "ROLE_ADMIN", "ROLE_USER")
    public List<String> getAllRoleNames() {
        return roleRepository.findAll().stream()
                .map(role -> role.getName().replace("ROLE_", "")) // Bỏ prefix ROLE_
                .collect(Collectors.toList());
    }
}