package com.example.devhire.dto;

import java.time.LocalDateTime;

public record ResumeResponse(
        Long id,
        Long candidateProfileId,
        String originalFileName,
        String contentType,
        Long fileSize,
        LocalDateTime uploadedAt
) {
}