package com.example.devhire.dto.candidate;

import jakarta.validation.constraints.Size;

public record UpdateCandidateProfileRequest(

        @Size(max = 30, message = "Le numéro de téléphone ne doit pas dépasser 30 caractères.") String phone,

        @Size(max = 100, message = "La ville ne doit pas dépasser 100 caractères.") String city) {
}