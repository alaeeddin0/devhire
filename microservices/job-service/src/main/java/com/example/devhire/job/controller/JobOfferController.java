package com.example.devhire.job.controller;

import com.example.devhire.job.dto.JobOfferRequest;
import com.example.devhire.job.dto.JobOfferResponse;
import com.example.devhire.job.service.JobOfferService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.devhire.job.security.AuthenticatedUser;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/job-offers")
@RequiredArgsConstructor
public class JobOfferController {

        private final JobOfferService jobOfferService;

        @GetMapping
        public ResponseEntity<Page<JobOfferResponse>> search(
                        @RequestParam(required = false) String keyword,
                        @RequestParam(required = false) String location,
                        @RequestParam(required = false) String workMode,
                        @RequestParam(required = false) String offerType,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size) {

                return ResponseEntity.ok(
                                jobOfferService.search(
                                                keyword,
                                                location,
                                                workMode,
                                                offerType,
                                                page,
                                                size));
        }

        @GetMapping("/{id}")
        public ResponseEntity<JobOfferResponse> getById(
                        @PathVariable Long id) {

                return ResponseEntity.ok(jobOfferService.getById(id));
        }

        /*
         * Temporaire : recruiterUserId doit venir du JWT,
         * jamais du client dans la version sécurisée.
         */
        @PostMapping
        public ResponseEntity<JobOfferResponse> create(
                        Authentication authentication,
                        @Valid @RequestBody JobOfferRequest request) {

                AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();

                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(jobOfferService.create(
                                                user.id(),
                                                request));
        }

        @PutMapping("/{id}")
        public ResponseEntity<JobOfferResponse> update(
                        @PathVariable Long id,
                        Authentication authentication,
                        @Valid @RequestBody JobOfferRequest request) {

                AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();

                return ResponseEntity.ok(
                                jobOfferService.update(
                                                id,
                                                user.id(),
                                                request));
        }

        @DeleteMapping("/{id}")
        public ResponseEntity<Void> delete(
                        @PathVariable Long id,
                        Authentication authentication) {

                AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();

                jobOfferService.delete(id, user.id());

                return ResponseEntity.noContent().build();
        }
}