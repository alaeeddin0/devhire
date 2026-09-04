package com.example.devhire.job.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record JobOfferResponse(
        Long id,
        String title,
        String description,
        String company,
        String location,
        String workMode,
        String offerType,
        BigDecimal salaryMin,
        BigDecimal salaryMax,
        LocalDateTime createdAt,
        Long recruiterUserId) {
}