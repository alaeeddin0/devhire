package com.example.devhire.interview.dto;

import com.example.devhire.interview.model.InterviewStatus;
import com.example.devhire.interview.model.InterviewType;

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
        LocalDateTime createdAt) {
}