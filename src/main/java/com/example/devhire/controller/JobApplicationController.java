package com.example.devhire.controller;

import com.example.devhire.dto.jobApplication.CreateJobApplicationRequest;
import com.example.devhire.dto.jobApplication.JobApplicationResponse;
import com.example.devhire.dto.jobApplication.UpdateApplicationStatusRequest;
import com.example.devhire.dto.jobApplication.UpdateJobApplicationRequest;
import com.example.devhire.service.JobApplicationService;
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

        @GetMapping("/{id}")
        public ResponseEntity<JobApplicationResponse> getApplicationById(
                        @PathVariable Long id,
                        Authentication authentication) {
                return ResponseEntity.ok(
                                jobApplicationService.getApplicationById(
                                                id,
                                                authentication.getName()));
        }

        @GetMapping("/me")
        public ResponseEntity<List<JobApplicationResponse>> getMyApplications(
                        Authentication authentication) {
                return ResponseEntity.ok(
                                jobApplicationService.getMyApplications(
                                                authentication.getName()));
        }

        @GetMapping("/received")
        public ResponseEntity<List<JobApplicationResponse>> getReceivedApplications(Authentication authentication) {
                return ResponseEntity.ok(
                                jobApplicationService.getReceivedApplications(
                                                authentication.getName()));
        }

        @PostMapping
        public ResponseEntity<JobApplicationResponse> createApplication(
                        Authentication authentication,
                        @Valid @RequestBody CreateJobApplicationRequest request) {
                return ResponseEntity
                                .status(HttpStatus.CREATED)
                                .body(
                                                jobApplicationService.createApplication(
                                                                authentication.getName(),
                                                                request));
        }

        @PutMapping("/{id}")
        public ResponseEntity<JobApplicationResponse> updateApplication(
                        @PathVariable Long id,
                        Authentication authentication,
                        @Valid @RequestBody UpdateJobApplicationRequest request) {
                return ResponseEntity.ok(
                                jobApplicationService.updateApplication(
                                                id,
                                                authentication.getName(),
                                                request));
        }

        @DeleteMapping("/{id}")
        public ResponseEntity<Void> deleteApplication(
                        @PathVariable Long id,
                        Authentication authentication) {
                jobApplicationService.deleteApplication(
                                id,
                                authentication.getName());

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
                                                authentication.getName(),
                                                request));
        }
}