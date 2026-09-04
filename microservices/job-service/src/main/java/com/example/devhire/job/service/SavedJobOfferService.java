package com.example.devhire.job.service;

import com.example.devhire.job.dto.JobOfferResponse;
import com.example.devhire.job.dto.SavedJobOfferResponse;
import com.example.devhire.job.exception.ResourceNotFoundException;
import com.example.devhire.job.model.JobOffer;
import com.example.devhire.job.model.SavedJobOffer;
import com.example.devhire.job.repo.JobOfferRepository;
import com.example.devhire.job.repo.SavedJobOfferRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SavedJobOfferService {

    private final SavedJobOfferRepository savedJobOfferRepository;
    private final JobOfferRepository jobOfferRepository;

    public SavedJobOfferResponse save(
            Long candidateUserId,
            Long jobOfferId) {

        if (savedJobOfferRepository
                .existsByCandidateUserIdAndJobOfferId(
                        candidateUserId,
                        jobOfferId)) {
            throw new IllegalArgumentException(
                    "Cette offre est déjà sauvegardée.");
        }

        JobOffer offer = getOffer(jobOfferId);

        SavedJobOffer savedOffer = new SavedJobOffer();
        savedOffer.setCandidateUserId(candidateUserId);
        savedOffer.setJobOffer(offer);

        return toResponse(savedJobOfferRepository.save(savedOffer));
    }

    @Transactional(readOnly = true)
    public List<SavedJobOfferResponse> getMySavedOffers(
            Long candidateUserId) {

        return savedJobOfferRepository
                .findAllByCandidateUserIdOrderBySavedAtDesc(
                        candidateUserId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public void delete(
            Long candidateUserId,
            Long savedOfferId) {

        SavedJobOffer savedOffer = savedJobOfferRepository
                .findById(savedOfferId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Offre sauvegardée introuvable."));

        if (!savedOffer.getCandidateUserId().equals(candidateUserId)) {
            throw new AccessDeniedException(
                    "Vous ne pouvez pas supprimer cette offre sauvegardée.");
        }

        savedJobOfferRepository.delete(savedOffer);
    }

    private JobOffer getOffer(Long jobOfferId) {
        return jobOfferRepository.findById(jobOfferId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Offre introuvable."));
    }

    private SavedJobOfferResponse toResponse(
            SavedJobOffer savedOffer) {

        JobOffer offer = savedOffer.getJobOffer();

        JobOfferResponse offerResponse = new JobOfferResponse(
                offer.getId(),
                offer.getTitle(),
                offer.getDescription(),
                offer.getCompany(),
                offer.getLocation(),
                offer.getWorkMode(),
                offer.getOfferType(),
                offer.getSalaryMin(),
                offer.getSalaryMax(),
                offer.getCreatedAt(),
                offer.getRecruiterUserId());

        return new SavedJobOfferResponse(
                savedOffer.getId(),
                savedOffer.getSavedAt(),
                offerResponse);
    }
}