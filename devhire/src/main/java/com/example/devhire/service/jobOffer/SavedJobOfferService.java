package com.example.devhire.service.jobOffer;

import com.example.devhire.dto.jobOffer.JobOfferResponse;
import com.example.devhire.dto.savedJobOffer.SavedJobOfferResponse;
import com.example.devhire.exception.ResourceNotFoundException;
import com.example.devhire.model.candidate.CandidateProfile;
import com.example.devhire.model.jobOffer.JobOffer;
import com.example.devhire.model.jobOffer.SavedJobOffer;
import com.example.devhire.repo.candidate.CandidateProfileRepository;
import com.example.devhire.repo.jobOffer.JobOfferRepository;
import com.example.devhire.repo.jobOffer.SavedJobOfferRepository;

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
    private final CandidateProfileRepository candidateProfileRepository;
    private final JobOfferRepository jobOfferRepository;

    public SavedJobOfferResponse saveJobOffer(String email, Long jobOfferId) {
        CandidateProfile candidate = getCandidateByEmail(email);
        JobOffer jobOffer = getJobOfferById(jobOfferId);

        if (savedJobOfferRepository.existsByCandidateIdAndJobOfferId(
                candidate.getId(), jobOfferId)) {
            throw new IllegalArgumentException(
                    "Cette offre est déjà sauvegardée.");
        }

        SavedJobOffer savedJobOffer = SavedJobOffer.builder()
                .candidate(candidate)
                .jobOffer(jobOffer)
                .build();

        return toResponse(savedJobOfferRepository.save(savedJobOffer));
    }

    @Transactional(readOnly = true)
    public List<SavedJobOfferResponse> getMySavedJobOffers(String email) {
        CandidateProfile candidate = getCandidateByEmail(email);

        return savedJobOfferRepository
                .findAllByCandidateIdOrderBySavedAtDesc(candidate.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public void deleteSavedJobOffer(String email, Long savedJobOfferId) {
        CandidateProfile candidate = getCandidateByEmail(email);

        SavedJobOffer savedJobOffer = savedJobOfferRepository
                .findById(savedJobOfferId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Offre sauvegardée introuvable."));

        if (!savedJobOffer.getCandidate().getId().equals(candidate.getId())) {
            throw new AccessDeniedException(
                    "Vous ne pouvez pas supprimer cette offre sauvegardée.");
        }

        savedJobOfferRepository.delete(savedJobOffer);
    }

    private CandidateProfile getCandidateByEmail(String email) {
        return candidateProfileRepository.findByUserEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Profil candidat introuvable."));
    }

    private JobOffer getJobOfferById(Long jobOfferId) {
        return jobOfferRepository.findById(jobOfferId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Offre introuvable."));
    }

    private SavedJobOfferResponse toResponse(SavedJobOffer savedJobOffer) {
        JobOffer jobOffer = savedJobOffer.getJobOffer();

        JobOfferResponse jobOfferResponse = new JobOfferResponse(
                jobOffer.getId(),
                jobOffer.getTitle(),
                jobOffer.getDescription(),
                jobOffer.getCompany(),
                jobOffer.getLocation(),
                jobOffer.getWorkMode(),
                jobOffer.getOfferType(),
                jobOffer.getSalaryMin(),
                jobOffer.getSalaryMax(),
                jobOffer.getCreatedAt(),
                jobOffer.getRecruiter().getId());

        return SavedJobOfferResponse.builder()
                .id(savedJobOffer.getId())
                .savedAt(savedJobOffer.getSavedAt())
                .jobOffer(jobOfferResponse)
                .build();
    }
}
