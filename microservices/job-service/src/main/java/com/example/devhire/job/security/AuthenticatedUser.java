package com.example.devhire.job.security;

public record AuthenticatedUser(
        Long id,
        String email,
        String role) {
}