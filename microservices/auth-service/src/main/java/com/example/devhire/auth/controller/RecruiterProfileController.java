package com.example.devhire.auth.controller;

import com.example.devhire.auth.dto.profile.CreateRecruiterProfileRequest;
import com.example.devhire.auth.dto.profile.RecruiterProfileResponse;
import com.example.devhire.auth.dto.profile.UpdateRecruiterProfileRequest;
import com.example.devhire.auth.service.RecruiterProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recruiter-profiles")
@RequiredArgsConstructor
public class RecruiterProfileController {

    private final RecruiterProfileService recruiterProfileService;

    @PostMapping
    public ResponseEntity<RecruiterProfileResponse> create(
            Authentication authentication,
            @Valid @RequestBody CreateRecruiterProfileRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(recruiterProfileService.create(
                        authentication.getName(),
                        request));
    }

    @GetMapping("/me")
    public ResponseEntity<RecruiterProfileResponse> getMyProfile(
            Authentication authentication) {

        return ResponseEntity.ok(
                recruiterProfileService.getMyProfile(
                        authentication.getName()));
    }

    @PutMapping("/me")
    public ResponseEntity<RecruiterProfileResponse> update(
            Authentication authentication,
            @Valid @RequestBody UpdateRecruiterProfileRequest request) {

        return ResponseEntity.ok(
                recruiterProfileService.update(
                        authentication.getName(),
                        request));
    }
}