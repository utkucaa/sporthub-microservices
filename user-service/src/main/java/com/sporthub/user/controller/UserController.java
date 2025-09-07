package com.sporthub.user.controller;

import com.sporthub.user.dto.request.UserRegistrationRequest;
import com.sporthub.user.dto.response.UserResponse;
import com.sporthub.user.model.Role;
import com.sporthub.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerUser(@Valid @RequestBody UserRegistrationRequest request) {
        UserResponse user = userService.createUser(request);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(user -> ResponseEntity.ok(user))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/profile")
    public ResponseEntity<UserResponse> getCurrentUserProfile() {
        return ResponseEntity.ok().build();
    }
    
    @PutMapping("/profile")
    public ResponseEntity<UserResponse> updateUserProfile(@Valid @RequestBody UserRegistrationRequest request) {
        return ResponseEntity.ok().build();
    }
    
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }
    
    @GetMapping("/active")
    public ResponseEntity<List<UserResponse>> getActiveUsers() {
        List<UserResponse> users = userService.getActiveUsers();
        return ResponseEntity.ok(users);
    }
    
    @GetMapping("/role/{role}")
    public ResponseEntity<List<UserResponse>> getUsersByRole(@PathVariable Role role) {
        List<UserResponse> users = userService.getUsersByRole(role);
        return ResponseEntity.ok(users);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id, @Valid @RequestBody UserRegistrationRequest request) {
        UserResponse user = userService.updateUser(id, request);
        return ResponseEntity.ok(user);
    }
    
    @PostMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateUser(@PathVariable Long id) {
        userService.deactivateUser(id);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/{id}/activate")
    public ResponseEntity<Void> activateUser(@PathVariable Long id) {
        userService.activateUser(id);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/{id}/role/{role}")
    public ResponseEntity<Void> changeUserRole(@PathVariable Long id, @PathVariable Role role) {
        userService.changeUserRole(id, role);
        return ResponseEntity.ok().build();
    }
}
