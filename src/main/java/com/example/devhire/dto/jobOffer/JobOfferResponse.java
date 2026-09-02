package com.example.devhire.dto.jobOffer;

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
        Long recruiterProfileId
) {
}
