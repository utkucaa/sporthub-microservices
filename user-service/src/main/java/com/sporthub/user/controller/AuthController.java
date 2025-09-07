package com.sporthub.user.controller;

import com.sporthub.user.dto.request.UserLoginRequest;
import com.sporthub.user.dto.response.AuthResponse;
import com.sporthub.user.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    
    @Autowired
    private AuthService authService;
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody UserLoginRequest request) {
        log.info("Login attempt for email={}", request.getEmail());
        try {
            AuthResponse response = authService.authenticateUser(request);
            log.info("Login success for email={}", request.getEmail());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException ex) {
            log.warn("Login bad request for email={}, error={}", request.getEmail(), ex.getMessage());
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (RuntimeException ex) {
            log.warn("Login unauthorized for email={}, error={}", request.getEmail(), ex.getMessage());
            return ResponseEntity.status(401).body(ex.getMessage());
        } catch (Exception ex) {
            log.error("Login failed for email={}", request.getEmail(), ex);
            return ResponseEntity.status(500).body("Login failed");
        }
    }
    
    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@RequestHeader("Authorization") String token) {      
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/validate")
    public ResponseEntity<Boolean> validateToken(@RequestHeader("Authorization") String token) {
        try {
            authService.validateToken(token.replace("Bearer ", ""));
            return ResponseEntity.ok(true);
        } catch (Exception e) {
            return ResponseEntity.ok(false);
        }
    }
}
