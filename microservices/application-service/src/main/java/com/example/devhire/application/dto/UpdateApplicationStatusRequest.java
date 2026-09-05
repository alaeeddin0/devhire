package com.example.devhire.application.dto;

import com.example.devhire.application.model.ApplicationStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateApplicationStatusRequest(

        @NotNull(message = "Le nouveau statut est obligatoire.") ApplicationStatus status) {
}