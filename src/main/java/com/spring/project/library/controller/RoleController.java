package com.spring.project.library.controller;

import com.spring.project.library.service.RoleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/roles")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    // Endpoint này sẽ được gọi từ Frontend khi mở Modal Edit
    @GetMapping
    public ResponseEntity<List<String>> getAllRoles() {
        // Chỉ ADMIN mới có thể truy cập endpoint này thông qua Security Config
        List<String> roleNames = roleService.getAllRoleNames();
        return ResponseEntity.ok(roleNames);
    }
}