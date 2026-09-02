package com.example.devhire.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.devhire.model.JobApplication;

public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {

        boolean existsByCandidateIdAndJobOfferId(
                        Long candidateId,
                        Long jobOfferId);

        List<JobApplication> findAllByCandidateIdOrderByAppliedAtDesc(
                        Long candidateProfileId);

        List<JobApplication> findAllByJobOfferRecruiterIdOrderByAppliedAtDesc(
                        Long recruiterProfileId);

        boolean existsByResumeId(Long resumeId);
}
