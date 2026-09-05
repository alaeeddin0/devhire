package com.example.devhire.interview.controller;

import com.example.devhire.interview.dto.CreateInterviewRequest;
import com.example.devhire.interview.dto.InterviewResponse;
import com.example.devhire.interview.dto.UpdateInterviewRequest;
import com.example.devhire.interview.security.AuthenticatedUser;
import com.example.devhire.interview.service.InterviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/interviews")
@RequiredArgsConstructor
public class InterviewController {

    private final InterviewService interviewService;

    @PostMapping
    public ResponseEntity<InterviewResponse> create(
            Authentication authentication,
            @Valid @RequestBody CreateInterviewRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(interviewService.create(
                        currentUser(authentication).id(),
                        request));
    }

    @GetMapping("/candidate/me")
    public ResponseEntity<List<InterviewResponse>> getMyCandidateInterviews(
            Authentication authentication) {

        return ResponseEntity.ok(
                interviewService.getMyCandidateInterviews(
                        currentUser(authentication).id()));
    }

    @GetMapping("/recruiter/me")
    public ResponseEntity<List<InterviewResponse>> getMyRecruiterInterviews(
            Authentication authentication) {

        return ResponseEntity.ok(
                interviewService.getMyRecruiterInterviews(
                        currentUser(authentication).id()));
    }

    @PutMapping("/{interviewId}")
    public ResponseEntity<InterviewResponse> update(
            @PathVariable Long interviewId,
            Authentication authentication,
            @Valid @RequestBody UpdateInterviewRequest request) {

        return ResponseEntity.ok(
                interviewService.update(
                        interviewId,
                        currentUser(authentication).id(),
                        request));
    }

    @PatchMapping("/{interviewId}/cancel")
    public ResponseEntity<InterviewResponse> cancel(
            @PathVariable Long interviewId,
            Authentication authentication) {

        return ResponseEntity.ok(
                interviewService.cancel(
                        interviewId,
                        currentUser(authentication).id()));
    }

    @PatchMapping("/{interviewId}/complete")
    public ResponseEntity<InterviewResponse> complete(
            @PathVariable Long interviewId,
            Authentication authentication) {

        return ResponseEntity.ok(
                interviewService.complete(
                        interviewId,
                        currentUser(authentication).id()));
    }

    private AuthenticatedUser currentUser(
            Authentication authentication) {

        return (AuthenticatedUser) authentication.getPrincipal();
    }
}