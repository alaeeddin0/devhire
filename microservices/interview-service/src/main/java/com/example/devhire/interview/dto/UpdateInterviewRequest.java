package com.example.devhire.interview.dto;

import com.example.devhire.interview.model.InterviewType;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record UpdateInterviewRequest(

        @NotNull InterviewType type,

        @NotNull @Future LocalDateTime scheduledAt,

        @NotNull @Min(15) @Max(480) Integer durationMinutes,

        @Size(max = 500) String meetingLink,

        @Size(max = 255) String location,

        @Size(max = 2000) String notes) {
}