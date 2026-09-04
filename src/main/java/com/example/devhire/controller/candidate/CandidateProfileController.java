package com.example.devhire.controller.candidate;

import com.example.devhire.dto.candidate.CandidateProfileResponse;
import com.example.devhire.dto.candidate.CreateCandidateProfileRequest;
import com.example.devhire.dto.candidate.UpdateCandidateProfileRequest;
import com.example.devhire.service.candidate.CandidateProfileService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/candidate-profiles")
public class CandidateProfileController {

        private final CandidateProfileService candidateProfileService;

        @GetMapping("/me")
        public ResponseEntity<CandidateProfileResponse> getCurrentProfile(
                        Authentication authentication) {
                return ResponseEntity.ok(
                                candidateProfileService.getCurrentProfile(
                                                authentication.getName()));
        }

        @PostMapping
        public ResponseEntity<CandidateProfileResponse> createProfile(
                        Authentication authentication,
                        @Valid @RequestBody CreateCandidateProfileRequest request) {
                return ResponseEntity
                                .status(HttpStatus.CREATED)
                                .body(
                                                candidateProfileService.createProfile(
                                                                authentication.getName(),
                                                                request));
        }

        @PutMapping("/me")
        public ResponseEntity<CandidateProfileResponse> updateCurrentProfile(
                        Authentication authentication,
                        @Valid @RequestBody UpdateCandidateProfileRequest request) {
                return ResponseEntity.ok(
                                candidateProfileService.updateCurrentProfile(
                                                authentication.getName(),
                                                request));
        }
}
