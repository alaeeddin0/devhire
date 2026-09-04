package com.example.devhire.auth.controller;

import com.example.devhire.auth.dto.auth.AuthResponse;
import com.example.devhire.auth.dto.auth.LoginRequest;
import com.example.devhire.auth.dto.auth.RegisterRequest;
import com.example.devhire.auth.dto.auth.UserResponse;
import com.example.devhire.auth.service.AuthService;
import com.example.devhire.auth.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(
            @Valid @RequestBody RegisterRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.register(request));
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(
            Authentication authentication) {
        return ResponseEntity.ok(
                userService.getCurrentUser(authentication.getName()));
    }
    
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request) {

        return ResponseEntity.ok(authService.login(request));
    }
    
    
}