package com.example.devhire.dto.interview;

import com.example.devhire.model.interview.InterviewType;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record CreateInterviewRequest(
        @NotNull(message = "L'identifiant de la candidature est obligatoire.") Long jobApplicationId,
        @NotNull(message = "Le type d'entretien est obligatoire.") InterviewType type,
        @NotNull(message = "La date de l'entretien est obligatoire.")
        @Future(message = "La date de l'entretien doit être dans le futur.") LocalDateTime scheduledAt,
        @NotNull(message = "La durée est obligatoire.")
        @Min(value = 15, message = "La durée minimale est de 15 minutes.")
        @Max(value = 480, message = "La durée maximale est de 480 minutes.") Integer durationMinutes,
        @Size(max = 500, message = "Le lien ne peut pas dépasser 500 caractères.") String meetingLink,
        @Size(max = 255, message = "Le lieu ne peut pas dépasser 255 caractères.") String location,
        @Size(max = 2000, message = "Les notes ne peuvent pas dépasser 2000 caractères.") String notes
) {
}
