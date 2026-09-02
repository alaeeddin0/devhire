package com.example.devhire.controller;

import com.example.devhire.dto.candidate.CandidateProfileResponse;
import com.example.devhire.dto.candidate.CreateCandidateProfileRequest;
import com.example.devhire.dto.candidate.UpdateCandidateProfileRequest;
import com.example.devhire.service.CandidateProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/candidate-profiles")
public class CandidateProfileController {

    private final CandidateProfileService candidateProfileService;

    @GetMapping("/{id}")
    public ResponseEntity<CandidateProfileResponse> getProfileById(
            @PathVariable Long id) {
        return ResponseEntity.ok(
                candidateProfileService.getProfileById(id));
    }

    @PostMapping
    public ResponseEntity<CandidateProfileResponse> createProfile(
            @Valid @RequestBody CreateCandidateProfileRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(candidateProfileService.createProfile(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CandidateProfileResponse> updateProfile(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCandidateProfileRequest request) {
        return ResponseEntity.ok(
                candidateProfileService.updateProfile(id, request));
    }
}
