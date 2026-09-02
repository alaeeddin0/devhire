package com.example.devhire.controller;

import com.example.devhire.dto.jobApplication.CreateJobApplicationRequest;
import com.example.devhire.dto.jobApplication.JobApplicationResponse;
import com.example.devhire.dto.jobApplication.UpdateApplicationStatusRequest;
import com.example.devhire.dto.jobApplication.UpdateJobApplicationRequest;
import com.example.devhire.service.JobApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/job-applications")
@RequiredArgsConstructor
public class JobApplicationController {

        private final JobApplicationService jobApplicationService;

        @GetMapping("/candidate/{candidateProfileId}")
        public ResponseEntity<List<JobApplicationResponse>> getApplicationsByCandidate(
                        @PathVariable Long candidateProfileId) {
                return ResponseEntity.ok(
                                jobApplicationService.getApplicationsByCandidate(
                                                candidateProfileId));
        }

        @GetMapping("/recruiter/{recruiterProfileId}")
        public ResponseEntity<List<JobApplicationResponse>> getApplicationsByRecruiter(
                        @PathVariable Long recruiterProfileId) {
                return ResponseEntity.ok(
                                jobApplicationService.getApplicationsByRecruiter(
                                                recruiterProfileId));
        }

        @GetMapping("/{id}")
        public ResponseEntity<JobApplicationResponse> getApplicationById(
                        @PathVariable Long id) {
                return ResponseEntity.ok(
                                jobApplicationService.getApplicationById(id));
        }

        @PutMapping("/{id}")
        public ResponseEntity<JobApplicationResponse> updateApplication(
                        @PathVariable Long id,
                        @RequestParam Long candidateProfileId,
                        @Valid @RequestBody UpdateJobApplicationRequest request) {
                return ResponseEntity.ok(
                                jobApplicationService.updateApplication(
                                                id,
                                                candidateProfileId,
                                                request));
        }

        @DeleteMapping("/{id}")
        public ResponseEntity<Void> deleteApplication(
                        @PathVariable Long id,
                        @RequestParam Long candidateProfileId) {
                jobApplicationService.deleteApplication(
                                id,
                                candidateProfileId);

                return ResponseEntity.noContent().build();
        }

        @PostMapping
        public ResponseEntity<JobApplicationResponse> createApplication(
                        @Valid @RequestBody CreateJobApplicationRequest request) {
                return ResponseEntity
                                .status(HttpStatus.CREATED)
                                .body(jobApplicationService.createApplication(request));
        }

        @PatchMapping("/{applicationId}/status")
        public ResponseEntity<JobApplicationResponse> updateStatus(
                        @PathVariable Long applicationId,
                        @RequestParam Long recruiterProfileId,
                        @Valid @RequestBody UpdateApplicationStatusRequest request) {
                return ResponseEntity.ok(
                                jobApplicationService.updateStatus(
                                                applicationId,
                                                recruiterProfileId,
                                                request));
        }
}
