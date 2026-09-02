package com.example.devhire.controller;

import com.example.devhire.dto.recruiter.CreateRecruiterProfileRequest;
import com.example.devhire.dto.recruiter.RecruiterProfileResponse;
import com.example.devhire.dto.recruiter.UpdateRecruiterProfileRequest;
import com.example.devhire.service.RecruiterProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recruiter-profiles")
@RequiredArgsConstructor
public class RecruiterProfileController {

    private final RecruiterProfileService recruiterProfileService;

    @GetMapping("/{id}")
    public ResponseEntity<RecruiterProfileResponse> getProfileById(
            @PathVariable Long id) {
        return ResponseEntity.ok(
                recruiterProfileService.getProfileById(id));
    }

    @PostMapping
    public ResponseEntity<RecruiterProfileResponse> createProfile(
            @Valid @RequestBody CreateRecruiterProfileRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(recruiterProfileService.createProfile(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RecruiterProfileResponse> updateProfile(
            @PathVariable Long id,
            @Valid @RequestBody UpdateRecruiterProfileRequest request) {
        return ResponseEntity.ok(
                recruiterProfileService.updateProfile(id, request));
    }
}
