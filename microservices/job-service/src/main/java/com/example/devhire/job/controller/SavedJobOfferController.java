package com.example.devhire.job.controller;

import com.example.devhire.job.dto.SavedJobOfferResponse;
import com.example.devhire.job.security.AuthenticatedUser;
import com.example.devhire.job.service.SavedJobOfferService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/saved-job-offers")
@RequiredArgsConstructor
public class SavedJobOfferController {

    private final SavedJobOfferService savedJobOfferService;

    @PostMapping("/{jobOfferId}")
    public ResponseEntity<SavedJobOfferResponse> save(
            @PathVariable Long jobOfferId,
            Authentication authentication) {

        AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(savedJobOfferService.save(
                        user.id(),
                        jobOfferId));
    }

    @GetMapping
    public ResponseEntity<List<SavedJobOfferResponse>> getMySavedOffers(
            Authentication authentication) {

        AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();

        return ResponseEntity.ok(
                savedJobOfferService.getMySavedOffers(user.id()));
    }

    @DeleteMapping("/{savedOfferId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long savedOfferId,
            Authentication authentication) {

        AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();

        savedJobOfferService.delete(user.id(), savedOfferId);

        return ResponseEntity.noContent().build();
    }
}