package com.example.devhire.dto.recruiter;

public record RecruiterProfileResponse(
        Long id,
        Long userId,
        String companyName,
        String companyDescription,
        String companyWebsite
) {
}
