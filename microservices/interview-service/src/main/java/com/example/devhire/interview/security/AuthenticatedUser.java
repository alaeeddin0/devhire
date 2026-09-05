package com.example.devhire.interview.security;

public record AuthenticatedUser(
        Long id,
        String email,
        String role) {
}