package com.example.devhire.dto.user;

import com.example.devhire.model.UserRole;

public record UserResponse(
                Long id,
                String firstName,
                String lastName,
                String email,
                UserRole role,
                boolean active) {
}
