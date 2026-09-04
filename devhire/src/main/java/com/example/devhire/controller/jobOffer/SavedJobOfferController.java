package com.example.devhire.controller.jobOffer;

import com.example.devhire.dto.savedJobOffer.SavedJobOfferResponse;
import com.example.devhire.service.jobOffer.SavedJobOfferService;

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
    public ResponseEntity<SavedJobOfferResponse> saveJobOffer(
            @PathVariable Long jobOfferId,
            Authentication authentication) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(savedJobOfferService.saveJobOffer(
                        authentication.getName(),
                        jobOfferId));
    }

    @GetMapping
    public ResponseEntity<List<SavedJobOfferResponse>> getMySavedJobOffers(
            Authentication authentication) {

        return ResponseEntity.ok(
                savedJobOfferService.getMySavedJobOffers(
                        authentication.getName()));
    }

    @DeleteMapping("/{savedJobOfferId}")
    public ResponseEntity<Void> deleteSavedJobOffer(
            @PathVariable Long savedJobOfferId,
            Authentication authentication) {

        savedJobOfferService.deleteSavedJobOffer(
                authentication.getName(),
                savedJobOfferId);

        return ResponseEntity.noContent().build();
    }
}