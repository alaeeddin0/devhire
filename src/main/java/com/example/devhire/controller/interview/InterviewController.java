package com.example.devhire.controller.interview;

import com.example.devhire.dto.interview.CreateInterviewRequest;
import com.example.devhire.dto.interview.InterviewResponse;
import com.example.devhire.dto.interview.UpdateInterviewRequest;
import com.example.devhire.service.interview.InterviewService;

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
    public ResponseEntity<InterviewResponse> createInterview(
            Authentication authentication,
            @Valid @RequestBody CreateInterviewRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(interviewService.createInterview(
                        authentication.getName(),
                        request));
    }

    @GetMapping("/candidate/me")
    public ResponseEntity<List<InterviewResponse>> getMyCandidateInterviews(
            Authentication authentication) {

        return ResponseEntity.ok(
                interviewService.getMyCandidateInterviews(
                        authentication.getName()));
    }

    @GetMapping("/recruiter/me")
    public ResponseEntity<List<InterviewResponse>> getMyRecruiterInterviews(
            Authentication authentication) {

        return ResponseEntity.ok(
                interviewService.getMyRecruiterInterviews(
                        authentication.getName()));
    }

    @PutMapping("/{interviewId}")
    public ResponseEntity<InterviewResponse> updateInterview(
            @PathVariable Long interviewId,
            Authentication authentication,
            @Valid @RequestBody UpdateInterviewRequest request) {

        return ResponseEntity.ok(
                interviewService.updateInterview(
                        interviewId,
                        authentication.getName(),
                        request));
    }

    @PatchMapping("/{interviewId}/cancel")
    public ResponseEntity<InterviewResponse> cancelInterview(
            @PathVariable Long interviewId,
                    Authentication authentication) {

            return ResponseEntity.ok(
                            interviewService.cancelInterview(
                                            interviewId,
                                            authentication.getName()));
    }
    
    @PatchMapping("/{interviewId}/complete")
    public ResponseEntity<InterviewResponse> completeInterview(
                    @PathVariable Long interviewId,
                    Authentication authentication) {

            return ResponseEntity.ok(
                            interviewService.completeInterview(
                                            interviewId,
                                            authentication.getName()));
    }
}