package com.example.devhire.dto.jobOffer;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record UpdateJobOfferRequest(

        @NotBlank(message = "Le titre est obligatoire.") @Size(max = 150, message = "Le titre ne doit pas dépasser 150 caractères.") String title,

        @NotBlank(message = "La description est obligatoire.") String description,

        @NotBlank(message = "Le nom de l'entreprise est obligatoire.") String company,

        @NotBlank(message = "La localisation est obligatoire.") String location,

        @NotBlank(message = "Le mode de travail est obligatoire.") String workMode,

        @NotBlank(message = "Le type d'offre est obligatoire.") String offerType,

        @NotNull(message = "Le salaire minimum est obligatoire.") @DecimalMin(value = "0.0", inclusive = true, message = "Le salaire minimum doit être positif.") BigDecimal salaryMin,

        @NotNull(message = "Le salaire maximum est obligatoire.") @DecimalMin(value = "0.0", inclusive = true, message = "Le salaire maximum doit être positif.") BigDecimal salaryMax) {
}