package com.example.devhire.controller.recruiter;

import com.example.devhire.dto.recruiter.CreateRecruiterProfileRequest;
import com.example.devhire.dto.recruiter.RecruiterProfileResponse;
import com.example.devhire.dto.recruiter.UpdateRecruiterProfileRequest;
import com.example.devhire.service.recruiter.RecruiterProfileService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/recruiter-profiles")
@RequiredArgsConstructor
public class RecruiterProfileController {

        private final RecruiterProfileService recruiterProfileService;

        @GetMapping("/me")
        public ResponseEntity<RecruiterProfileResponse> getCurrentProfile(
                        Authentication authentication) {
                return ResponseEntity.ok(
                                recruiterProfileService.getCurrentProfile(
                                                authentication.getName()));
        }

        @PostMapping
        public ResponseEntity<RecruiterProfileResponse> createProfile(
                        Authentication authentication,
                        @Valid @RequestBody CreateRecruiterProfileRequest request) {
                return ResponseEntity
                                .status(HttpStatus.CREATED)
                                .body(recruiterProfileService.createProfile(authentication.getName(), request));
        }

        @PutMapping("/me")
        public ResponseEntity<RecruiterProfileResponse> updateCurrentProfile(
                        Authentication authentication,
                        @Valid @RequestBody UpdateRecruiterProfileRequest request) {
                return ResponseEntity.ok(
                                recruiterProfileService.updateCurrentProfile(
                                                authentication.getName(),
                                                request));
        }
}
