package com.example.devhire.dto.interview;

import com.example.devhire.model.interview.InterviewStatus;
import com.example.devhire.model.interview.InterviewType;

import java.time.LocalDateTime;

public record InterviewResponse(
        Long id,
        Long jobApplicationId,
        InterviewType type,
        InterviewStatus status,
        LocalDateTime scheduledAt,
        Integer durationMinutes,
        String meetingLink,
        String location,
        String notes,
        LocalDateTime createdAt
) {
}
