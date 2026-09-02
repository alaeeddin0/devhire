package com.example.devhire.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateUserRequest(

        @NotBlank(message = "Le prénom est obligatoire.") String firstName,

        @NotBlank(message = "Le nom est obligatoire.") String lastName,

        @NotBlank(message = "L'email est obligatoire.") @Email(message = "Format d'email invalide.") String email,

        @Size(min = 8, message = "Le nouveau mot de passe doit contenir au moins 8 caractères.") String password) {
}