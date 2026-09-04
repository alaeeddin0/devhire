package com.example.devhire.auth.dto.profile;

import jakarta.validation.constraints.Size;

public record UpdateCandidateProfileRequest(
        @Size(max = 30) String phone,
        @Size(max = 100) String city) {
}