package com.example.devhire.dto.jobApplication;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateJobApplicationRequest(

        @NotNull(message = "L'identifiant du profil candidat est obligatoire.")
        Long candidateProfileId,

        @NotNull(message = "L'identifiant de l'offre est obligatoire.")
        Long jobOfferId,

        @Size(max = 2000, message = "La lettre de motivation est trop longue.")
        String coverLetter,
        
        @NotNull(message = "Le CV est obligatoire.")
        Long resumeId
) {
}