package com.example.devhire.application.dto;

import com.example.devhire.application.model.ApplicationStatus;

import java.time.LocalDateTime;

public record ApplicationStatusHistoryResponse(
        Long id,
        ApplicationStatus previousStatus,
        ApplicationStatus newStatus,
        Long changedByRecruiterUserId,
        LocalDateTime changedAt) {
}