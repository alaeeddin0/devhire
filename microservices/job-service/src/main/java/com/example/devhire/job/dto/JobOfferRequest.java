package com.example.devhire.job.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record JobOfferRequest(

        @NotBlank @Size(max = 150) String title,

        @NotBlank @Size(max = 10000) String description,

        @NotBlank @Size(max = 150) String company,

        @NotBlank @Size(max = 100) String location,

        @NotBlank @Size(max = 30) String workMode,

        @NotBlank @Size(max = 30) String offerType,

        @DecimalMin(value = "0.0", inclusive = true) BigDecimal salaryMin,

        @DecimalMin(value = "0.0", inclusive = true) BigDecimal salaryMax) {
}