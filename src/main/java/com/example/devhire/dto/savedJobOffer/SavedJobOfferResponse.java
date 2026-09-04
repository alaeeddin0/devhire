package com.example.devhire.dto.savedJobOffer;

import com.example.devhire.dto.jobOffer.JobOfferResponse;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record SavedJobOfferResponse(
        Long id,
        LocalDateTime savedAt,
        JobOfferResponse jobOffer
) {
}
