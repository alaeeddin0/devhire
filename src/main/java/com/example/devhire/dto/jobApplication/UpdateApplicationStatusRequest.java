package com.example.devhire.dto.jobApplication;

import com.example.devhire.model.ApplicationStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateApplicationStatusRequest(

        @NotNull(message = "Le nouveau statut est obligatoire.") ApplicationStatus status) {
}
