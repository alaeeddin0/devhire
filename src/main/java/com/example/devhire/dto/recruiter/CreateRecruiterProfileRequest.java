package com.example.devhire.dto.recruiter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateRecruiterProfileRequest(

        @NotNull(message = "L'identifiant utilisateur est obligatoire.")
        Long userId,

        @NotBlank(message = "Le nom de l'entreprise est obligatoire.")
        String companyName,

        String companyDescription,

        String companyWebsite
) {
}