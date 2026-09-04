package com.example.devhire.auth.dto.auth;

import com.example.devhire.auth.model.UserRole;

public record AuthResponse(
        String token,
        String tokenType,
        Long userId,
        String email,
        UserRole role) {
}