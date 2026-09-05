package com.example.devhire.application.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateJobApplicationRequest(

                @NotNull(message = "L'identifiant de l'offre est obligatoire.") Long jobOfferId,

                @NotNull(message = "L'identifiant du CV est obligatoire.") Long resumeId,

                @Size(max = 5000, message = "La lettre de motivation ne peut pas dépasser 5000 caractères.") String coverLetter

) {

}