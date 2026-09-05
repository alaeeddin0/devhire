package com.example.devhire.application.repo;

import com.example.devhire.application.model.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobApplicationRepository
                extends JpaRepository<JobApplication, Long> {

        boolean existsByCandidateUserIdAndJobOfferId(
                        Long candidateUserId,
                        Long jobOfferId);

        List<JobApplication> findAllByCandidateUserIdOrderByAppliedAtDesc(
                        Long candidateUserId);

        List<JobApplication> findAllByJobOfferIdOrderByAppliedAtDesc(
                        Long jobOfferId);

        boolean existsByResumeId(Long resumeId);
}