package com.example.devhire.auth.dto.profile;

public record RecruiterProfileResponse(
        Long id,
        Long userId,
        String companyName,
        String companyDescription,
        String companyWebsite) {
}