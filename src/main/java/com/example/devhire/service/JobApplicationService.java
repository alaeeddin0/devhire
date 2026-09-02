package com.example.devhire.service;

import com.example.devhire.dto.jobApplication.CreateJobApplicationRequest;
import com.example.devhire.dto.jobApplication.JobApplicationResponse;
import com.example.devhire.dto.jobApplication.UpdateApplicationStatusRequest;
import com.example.devhire.exception.ResourceNotFoundException;
import com.example.devhire.model.ApplicationStatus;
import com.example.devhire.model.CandidateProfile;
import com.example.devhire.model.JobApplication;
import com.example.devhire.model.JobOffer;
import com.example.devhire.model.RecruiterProfile;
import com.example.devhire.model.Resume;
import com.example.devhire.repo.CandidateProfileRepository;
import com.example.devhire.repo.JobApplicationRepository;
import com.example.devhire.repo.JobOfferRepository;
import com.example.devhire.repo.RecruiterProfileRepository;
import com.example.devhire.repo.ResumeRepository;
import lombok.RequiredArgsConstructor;
import com.example.devhire.dto.jobApplication.UpdateJobApplicationRequest;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class JobApplicationService {

        private final JobApplicationRepository jobApplicationRepository;
        private final CandidateProfileRepository candidateProfileRepository;
        private final JobOfferRepository jobOfferRepository;
        private final ResumeRepository resumeRepository;
        private final RecruiterProfileRepository recruiterProfileRepository;

        public List<JobApplicationResponse> getApplicationsByCandidate(
                        Long candidateProfileId) {
                if (!candidateProfileRepository.existsById(candidateProfileId)) {
                        throw new ResourceNotFoundException(
                                        "Profil candidat introuvable avec l'id : "
                                                        + candidateProfileId);
                }

                return jobApplicationRepository
                                .findAllByCandidateIdOrderByAppliedAtDesc(candidateProfileId)
                                .stream()
                                .map(this::toResponse)
                                .toList();
        }

        public List<JobApplicationResponse> getApplicationsByRecruiter(
                        Long recruiterProfileId) {
                return jobApplicationRepository
                                .findAllByJobOfferRecruiterIdOrderByAppliedAtDesc(
                                                recruiterProfileId)
                                .stream()
                                .map(this::toResponse)
                                .toList();
        }
        
        public JobApplicationResponse getApplicationById(Long id) {
                return toResponse(getApplicationEntityById(id));
        }
        
        private JobApplication getApplicationEntityById(Long id) {
                return jobApplicationRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Candidature introuvable avec l'id : " + id));
        }


        @Transactional
        public JobApplicationResponse createApplication(
                        CreateJobApplicationRequest request) {
                CandidateProfile candidate = candidateProfileRepository
                                .findById(request.candidateProfileId())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Profil candidat introuvable avec l'id : "
                                                                + request.candidateProfileId()));

                JobOffer jobOffer = jobOfferRepository
                                .findById(request.jobOfferId())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Offre introuvable avec l'id : "
                                                                + request.jobOfferId()));

                if (jobApplicationRepository.existsByCandidateIdAndJobOfferId(
                                candidate.getId(),
                                jobOffer.getId())) {
                        throw new IllegalArgumentException(
                                        "Le candidat a déjà postulé à cette offre.");
                }
                Resume resume = resumeRepository.findById(request.resumeId())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "CV introuvable avec l'id : " + request.resumeId()));

                if (!resume.getCandidate().getId().equals(candidate.getId())) {
                        throw new IllegalArgumentException(
                                        "Ce CV n'appartient pas à ce candidat.");
                }

                JobApplication application = new JobApplication();
                application.setCandidate(candidate);
                application.setJobOffer(jobOffer);
                application.setCoverLetter(request.coverLetter());
                application.setStatus(ApplicationStatus.PENDING);
                application.setResume(resume);

                return toResponse(jobApplicationRepository.save(application));
        }

        @Transactional
        public JobApplicationResponse updateApplication(
                        Long applicationId,
                        Long candidateProfileId,
                        UpdateJobApplicationRequest request) {
                JobApplication application = getApplicationEntityById(applicationId);

                if (!application.getCandidate().getId().equals(candidateProfileId)) {
                        throw new IllegalArgumentException(
                                        "Ce candidat ne peut pas modifier cette candidature.");
                }

                if (application.getStatus() != ApplicationStatus.PENDING) {
                        throw new IllegalArgumentException(
                                        "Seule une candidature au statut PENDING peut être modifiée.");
                }

                Resume resume = resumeRepository.findById(request.resumeId())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "CV introuvable avec l'id : " + request.resumeId()));

                if (!resume.getCandidate().getId().equals(candidateProfileId)) {
                        throw new IllegalArgumentException(
                                        "Ce CV n'appartient pas à ce candidat.");
                }

                application.setResume(resume);
                application.setCoverLetter(request.coverLetter());

                return toResponse(jobApplicationRepository.save(application));
        }
        
        @Transactional
        public void deleteApplication(
                        Long applicationId,
                        Long candidateProfileId) {
                JobApplication application = getApplicationEntityById(applicationId);

                if (!application.getCandidate().getId().equals(candidateProfileId)) {
                        throw new IllegalArgumentException(
                                        "Ce candidat ne peut pas supprimer cette candidature.");
                }

                if (application.getStatus() != ApplicationStatus.PENDING) {
                        throw new IllegalArgumentException(
                                        "Seule une candidature au statut PENDING peut être supprimée.");
                }

                jobApplicationRepository.delete(application);
        }

        @Transactional
        public JobApplicationResponse updateStatus(
                        Long applicationId,
                        Long recruiterProfileId,
                        UpdateApplicationStatusRequest request) {
                JobApplication application = jobApplicationRepository
                                .findById(applicationId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Candidature introuvable avec l'id : " + applicationId));

                RecruiterProfile recruiter = recruiterProfileRepository
                                .findById(recruiterProfileId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Profil recruteur introuvable avec l'id : "
                                                                + recruiterProfileId));

                Long offerRecruiterId = application.getJobOffer()
                                .getRecruiter()
                                .getId();

                if (!offerRecruiterId.equals(recruiter.getId())) {
                        throw new IllegalArgumentException(
                                        "Ce recruteur ne peut pas modifier cette candidature.");
                }

                ApplicationStatus currentStatus = application.getStatus();
                ApplicationStatus newStatus = request.status();

                if (!isTransitionAllowed(currentStatus, newStatus)) {
                        throw new IllegalArgumentException(
                                        "Transition de statut non autorisée : "
                                                        + currentStatus + " -> " + newStatus);
                }

                application.setStatus(newStatus);

                return toResponse(jobApplicationRepository.save(application));
        }

        private JobApplicationResponse toResponse(
                        JobApplication application) {
                return new JobApplicationResponse(
                                application.getId(),
                                application.getCandidate().getId(),
                                application.getJobOffer().getId(),
                                application.getStatus(),
                                application.getCoverLetter(),
                                application.getAppliedAt());
        }
        
        private boolean isTransitionAllowed(
                        ApplicationStatus currentStatus,
                        ApplicationStatus newStatus) {
                return switch (currentStatus) {
                        case PENDING ->
                                newStatus == ApplicationStatus.REVIEWING
                                                || newStatus == ApplicationStatus.REJECTED;

                        case REVIEWING ->
                                newStatus == ApplicationStatus.INTERVIEW
                                                || newStatus == ApplicationStatus.ACCEPTED
                                                || newStatus == ApplicationStatus.REJECTED;

                        case INTERVIEW ->
                                newStatus == ApplicationStatus.ACCEPTED
                                                || newStatus == ApplicationStatus.REJECTED;

                        case ACCEPTED, REJECTED -> false;
                };
        }
}
