package com.example.devhire.auth.dto.profile;

public record CandidateProfileResponse(
        Long id,
        Long userId,
        String phone,
        String city) {
}