package com.example.devhire.dto.candidate;

public record CandidateProfileResponse(
        Long id,
        Long userId,
        String phone,
        String city
) {
}
