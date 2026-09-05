package com.example.devhire.application.dto;

import com.example.devhire.application.model.ApplicationStatus;

public record JobApplicationInternalResponse(
        Long id,
        Long candidateUserId,
        Long jobOfferId,
        ApplicationStatus status
) {
}