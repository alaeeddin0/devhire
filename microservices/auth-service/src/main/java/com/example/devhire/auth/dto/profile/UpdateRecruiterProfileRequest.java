package com.example.devhire.auth.dto.profile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateRecruiterProfileRequest(

        @NotBlank(message = "Le nom de l'entreprise est obligatoire.") @Size(max = 150) String companyName,

        @Size(max = 5000) String companyDescription,

        @Size(max = 255) String companyWebsite) {
}