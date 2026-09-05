package com.example.devhire.application.dto;

import com.example.devhire.application.model.ApplicationStatus;

import java.time.LocalDateTime;

public record JobApplicationResponse(
                Long id,
                Long candidateUserId,
                Long jobOfferId,
                Long resumeId,
                ApplicationStatus status,
                String coverLetter,
                LocalDateTime appliedAt) {
}