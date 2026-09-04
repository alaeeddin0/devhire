package com.example.devhire.dto.jobApplication;

import com.example.devhire.model.jobApplication.ApplicationStatus;

import java.time.LocalDateTime;

public record ApplicationStatusHistoryResponse(
        Long id,
        ApplicationStatus previousStatus,
        ApplicationStatus newStatus,
        Long changedByRecruiterId,
        LocalDateTime changedAt) {
}
