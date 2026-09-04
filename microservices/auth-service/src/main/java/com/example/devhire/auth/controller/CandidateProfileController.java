package com.example.devhire.auth.controller;

import com.example.devhire.auth.dto.profile.CandidateProfileResponse;
import com.example.devhire.auth.dto.profile.CreateCandidateProfileRequest;
import com.example.devhire.auth.dto.profile.UpdateCandidateProfileRequest;
import com.example.devhire.auth.service.CandidateProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/candidate-profiles")
@RequiredArgsConstructor
public class CandidateProfileController {

    private final CandidateProfileService candidateProfileService;

    @PostMapping
    public ResponseEntity<CandidateProfileResponse> create(
            Authentication authentication,
            @Valid @RequestBody CreateCandidateProfileRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(candidateProfileService.create(
                        authentication.getName(),
                        request));
    }

    @GetMapping("/me")
    public ResponseEntity<CandidateProfileResponse> getMyProfile(
            Authentication authentication) {

        return ResponseEntity.ok(
                candidateProfileService.getMyProfile(
                        authentication.getName()));
    }

    @PutMapping("/me")
    public ResponseEntity<CandidateProfileResponse> update(
            Authentication authentication,
            @Valid @RequestBody UpdateCandidateProfileRequest request) {

        return ResponseEntity.ok(
                candidateProfileService.update(
                        authentication.getName(),
                        request));
    }
}