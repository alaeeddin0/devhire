package com.example.devhire.dto.user;

import com.example.devhire.model.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateUserRequest(

        @NotBlank(message = "Le prénom est obligatoire.")
        String firstName,

        @NotBlank(message = "Le nom est obligatoire.")
        String lastName,

        @NotBlank(message = "L'email est obligatoire.")
        @Email(message = "Format d'email invalide.")
        String email,

        @NotBlank(message = "Le mot de passe est obligatoire.")
        @Size(min = 8, message = "Le mot de passe doit contenir au moins 8 caractères et contenir au moins une majuscule, une minuscule et un chiffre et un caractère spécial(!@#$%^&*()-+).")
        String password,

        @NotNull(message = "Le rôle est obligatoire.")
        UserRole role
) {
}
