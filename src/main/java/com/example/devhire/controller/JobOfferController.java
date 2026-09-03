package com.example.devhire.controller;

import com.example.devhire.dto.jobOffer.JobOfferResponse;
import com.example.devhire.dto.jobOffer.UpdateJobOfferRequest;
import com.example.devhire.model.JobOffer;
import com.example.devhire.service.JobOfferService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/job-offers")
public class JobOfferController {

        private final JobOfferService jobOfferService;

        @GetMapping
        public ResponseEntity<List<JobOfferResponse>> getAllJobOffers() {
                return ResponseEntity.ok(jobOfferService.getAllJobOffers());
        }

        @GetMapping("/{id}")
        public ResponseEntity<JobOfferResponse> getJobOfferById(
                        @PathVariable Long id) {
                return ResponseEntity.ok(jobOfferService.getJobOfferById(id));
        }

        @PostMapping
        public ResponseEntity<JobOfferResponse> createJobOffer(
                        Authentication authentication,
                        @RequestBody JobOffer jobOffer) {
                JobOfferResponse createdJobOffer = jobOfferService.createJobOffer(
                                authentication.getName(),
                                jobOffer);

                return ResponseEntity
                                .status(HttpStatus.CREATED)
                                .body(createdJobOffer);
        }

        @DeleteMapping("/{id}")
        public ResponseEntity<Void> deleteJobOffer(
                        @PathVariable Long id,
                        Authentication authentication) {
                jobOfferService.deleteJobOffer(
                                id,
                                authentication.getName());

                return ResponseEntity.noContent().build();
        }
        @GetMapping("/search")
        public ResponseEntity<Page<JobOfferResponse>> searchJobOffers(
                        @RequestParam(required = false) String keyword,
                        @RequestParam(required = false) String location,
                        @RequestParam(required = false) String workMode,
                        @RequestParam(required = false) String offerType,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size) {
                return ResponseEntity.ok(
                                jobOfferService.searchJobOffers(
                                                keyword,
                                                location,
                                                workMode,
                                                offerType,
                                                page,
                                                size));
        }

        @PutMapping("/{id}")
        public ResponseEntity<JobOfferResponse> updateJobOffer(
                        @PathVariable Long id,
                        Authentication authentication,
                        @Valid @RequestBody UpdateJobOfferRequest request) {
                return ResponseEntity.ok(
                                jobOfferService.updateJobOffer(
                                                id,
                                                authentication.getName(),
                                                request));
        }
}
