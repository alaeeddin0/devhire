package com.example.devhire.job.repo;

import com.example.devhire.job.model.SavedJobOffer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SavedJobOfferRepository
        extends JpaRepository<SavedJobOffer, Long> {

    boolean existsByCandidateUserIdAndJobOfferId(
            Long candidateUserId,
            Long jobOfferId);

    List<SavedJobOffer> findAllByCandidateUserIdOrderBySavedAtDesc(
            Long candidateUserId);
}