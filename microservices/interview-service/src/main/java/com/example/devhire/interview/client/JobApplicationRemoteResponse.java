package com.example.devhire.interview.client;

public record JobApplicationRemoteResponse(
        Long id,
        Long candidateUserId,
        Long jobOfferId,
        String status) {
}