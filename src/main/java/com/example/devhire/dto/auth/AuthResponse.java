package com.example.devhire.dto.auth;

import com.example.devhire.model.user.UserRole;

public record AuthResponse(
        String accessToken,
        String tokenType,
        long expiresIn,
        Long userId,
        String email,
        UserRole role) {
}
