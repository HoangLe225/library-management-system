package com.spring.project.library.controller;

import com.spring.project.library.model.Role;
import com.spring.project.library.model.User;
import com.spring.project.library.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    // GET all users
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users); // HTTP 200
    }

    // GET user by ID
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok) // HTTP 200
                .orElseGet(() -> ResponseEntity.notFound().build()); // HTTP 404
    }

    // POST create user
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User createdUser = userService.saveUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser); // HTTP 201
    }

    // PUT update user
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User userUpdate) {
        Optional<User> existing = userService.getUserById(id);
        if (existing.isEmpty()) {
            return ResponseEntity.notFound().build(); // HTTP 404
        }

        User updated = userService.saveUser(userUpdate);
        return ResponseEntity.ok(updated); // HTTP 200
    }

    // DELETE user
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        Optional<User> existing = userService.getUserById(id);
        if (existing.isEmpty()) {
            return ResponseEntity.notFound().build(); // HTTP 404
        }

        userService.deleteUser(id);
        return ResponseEntity.noContent().build(); // HTTP 204
    }
    @GetMapping("/{id}/roles")
    public ResponseEntity<List<Role>> getRolesForUser(@PathVariable Long id) {
        Optional<User> userOptional = userService.getUserById(id);
        if (userOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        List<Role> roles = userService.getRolesForUser(id);
        return ResponseEntity.ok(roles);
    }
}
