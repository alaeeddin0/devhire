package com.example.devhire.job.dto;

import java.time.LocalDateTime;

public record SavedJobOfferResponse(
        Long id,
        LocalDateTime savedAt,
        JobOfferResponse jobOffer) {
}