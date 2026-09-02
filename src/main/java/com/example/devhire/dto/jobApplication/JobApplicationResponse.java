package com.example.devhire.dto.jobApplication;

import com.example.devhire.model.ApplicationStatus;
import java.time.LocalDateTime;

public record JobApplicationResponse(
        Long id,
        Long candidateProfileId,
        Long jobOfferId,
        ApplicationStatus status,
        String coverLetter,
        LocalDateTime appliedAt
) {
}
