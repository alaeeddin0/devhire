package com.example.devhire.application.controller;

import com.example.devhire.application.dto.*;
import com.example.devhire.application.security.AuthenticatedUser;
import com.example.devhire.application.service.JobApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/job-applications")
@RequiredArgsConstructor
public class JobApplicationController {

        private final JobApplicationService jobApplicationService;

        @PostMapping
        public ResponseEntity<JobApplicationResponse> create(
                        Authentication authentication,
                        @Valid @RequestBody CreateJobApplicationRequest request) {

                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(jobApplicationService.create(
                                                currentUser(authentication).id(),
                                                request));
        }

        @GetMapping("/me")
        public ResponseEntity<List<JobApplicationResponse>> getMyApplications(
                        Authentication authentication) {

                return ResponseEntity.ok(
                                jobApplicationService.getMyApplications(
                                                currentUser(authentication).id()));
        }

        @GetMapping("/job-offer/{jobOfferId}")
        public ResponseEntity<List<JobApplicationResponse>> getReceivedApplications(
                        @PathVariable Long jobOfferId,
                        Authentication authentication) {

                return ResponseEntity.ok(
                                jobApplicationService.getReceivedApplications(
                                                jobOfferId,
                                                currentUser(authentication).id()));
        }

        @GetMapping("/{applicationId}/status-history")
        public ResponseEntity<List<ApplicationStatusHistoryResponse>> getStatusHistory(
                        @PathVariable Long applicationId,
                        Authentication authentication) {

                AuthenticatedUser user = currentUser(authentication);

                return ResponseEntity.ok(
                                jobApplicationService.getStatusHistory(
                                                applicationId,
                                                user.id(),
                                                user.role()));
        }

        @PutMapping("/{applicationId}")
        public ResponseEntity<JobApplicationResponse> update(
                        @PathVariable Long applicationId,
                        Authentication authentication,
                        @Valid @RequestBody UpdateJobApplicationRequest request) {

                return ResponseEntity.ok(
                                jobApplicationService.update(
                                                applicationId,
                                                currentUser(authentication).id(),
                                                request));
        }

        @DeleteMapping("/{applicationId}")
        public ResponseEntity<Void> delete(
                        @PathVariable Long applicationId,
                        Authentication authentication) {

                jobApplicationService.delete(
                                applicationId,
                                currentUser(authentication).id());

                return ResponseEntity.noContent().build();
        }

        @PatchMapping("/{applicationId}/status")
        public ResponseEntity<JobApplicationResponse> updateStatus(
                        @PathVariable Long applicationId,
                        Authentication authentication,
                        @Valid @RequestBody UpdateApplicationStatusRequest request) {

                return ResponseEntity.ok(
                                jobApplicationService.updateStatus(
                                                applicationId,
                                                currentUser(authentication).id(),
                                                request));
        }

        private AuthenticatedUser currentUser(
                        Authentication authentication) {

                return (AuthenticatedUser) authentication.getPrincipal();
        }
}