package com.example.devhire.dto.candidate;

import jakarta.validation.constraints.NotNull;

public record CreateCandidateProfileRequest(

        @NotNull(message = "L'identifiant utilisateur est obligatoire.")
        Long userId,

        String phone,

        String city
) {
}