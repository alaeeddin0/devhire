package com.example.devhire.application.security;

public record AuthenticatedUser(
        Long id,
        String email,
        String role) {
}