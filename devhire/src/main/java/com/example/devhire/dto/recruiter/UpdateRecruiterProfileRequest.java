package com.example.devhire.dto.recruiter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateRecruiterProfileRequest(

        @NotBlank(message = "Le nom de l'entreprise est obligatoire.") @Size(max = 150, message = "Le nom de l'entreprise ne doit pas dépasser 150 caractères.") String companyName,

        String companyDescription,

        @Size(max = 255, message = "Le site web ne doit pas dépasser 255 caractères.") String companyWebsite) {
}