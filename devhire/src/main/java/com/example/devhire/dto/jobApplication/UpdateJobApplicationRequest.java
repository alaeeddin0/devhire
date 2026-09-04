package com.example.devhire.dto.jobApplication;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateJobApplicationRequest(

        @NotNull(message = "Le CV est obligatoire.")
        Long resumeId,

        @Size(
                max = 2000,
                message = "La lettre de motivation est trop longue."
        )
        String coverLetter
) {
}