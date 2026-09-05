package com.example.devhire.interview.dto;

import com.example.devhire.interview.model.InterviewType;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record CreateInterviewRequest(

        @NotNull(message = "La candidature est obligatoire.") Long jobApplicationId,

        @NotNull(message = "Le type est obligatoire.") InterviewType type,

        @NotNull(message = "La date est obligatoire.") @Future(message = "La date doit être dans le futur.") LocalDateTime scheduledAt,

        @NotNull(message = "La durée est obligatoire.") @Min(15) @Max(480) Integer durationMinutes,

        @Size(max = 500) String meetingLink,

        @Size(max = 255) String location,

        @Size(max = 2000) String notes) {
}