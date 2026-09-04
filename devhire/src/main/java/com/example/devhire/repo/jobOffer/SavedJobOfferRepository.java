package com.example.devhire.repo.jobOffer;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.devhire.model.jobOffer.SavedJobOffer;

import java.util.List;
import java.util.Optional;

public interface SavedJobOfferRepository
        extends JpaRepository<SavedJobOffer, Long> {

    boolean existsByCandidateIdAndJobOfferId(
            Long candidateId,
            Long jobOfferId);

    Optional<SavedJobOffer> findByCandidateIdAndJobOfferId(
            Long candidateId,
            Long jobOfferId);

    List<SavedJobOffer> findAllByCandidateIdOrderBySavedAtDesc(
            Long candidateId);
}
