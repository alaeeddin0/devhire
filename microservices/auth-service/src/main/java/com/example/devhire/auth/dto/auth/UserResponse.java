package com.example.devhire.auth.dto.auth;

import com.example.devhire.auth.model.UserRole;

public record UserResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        UserRole role,
        boolean active) {
}