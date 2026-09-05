package com.example.devhire.application.service;

import com.example.devhire.application.client.JobServiceClient;
import com.example.devhire.application.dto.*;
import com.example.devhire.application.exception.ResourceNotFoundException;
import com.example.devhire.application.model.ApplicationStatus;
import com.example.devhire.application.model.JobApplication;
import com.example.devhire.application.repo.JobApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.devhire.application.model.Resume;
import com.example.devhire.application.repo.ResumeRepository;
import com.example.devhire.application.model.ApplicationStatusHistory;
import com.example.devhire.application.repo.ApplicationStatusHistoryRepository;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class JobApplicationService {

        private final JobApplicationRepository jobApplicationRepository;
        private final JobServiceClient jobServiceClient;
        private final ResumeRepository resumeRepository;
        private final ApplicationStatusHistoryRepository applicationStatusHistoryRepository;

        public JobApplicationResponse create(
                        Long candidateUserId,
                        CreateJobApplicationRequest request) {

                jobServiceClient.getOffer(request.jobOfferId());

                if (jobApplicationRepository.existsByCandidateUserIdAndJobOfferId(
                                candidateUserId, request.jobOfferId())) {
                        throw new IllegalArgumentException(
                                        "Vous avez déjà postulé à cette offre.");
                }
                Resume resume = getOwnedResume(
                                candidateUserId,
                                request.resumeId());

                JobApplication application = new JobApplication();
                application.setCandidateUserId(candidateUserId);
                application.setJobOfferId(request.jobOfferId());
                application.setResumeId(resume.getId());
                application.setCoverLetter(normalize(request.coverLetter()));
                application.setStatus(ApplicationStatus.PENDING);

                return toResponse(jobApplicationRepository.save(application));
        }

        @Transactional(readOnly = true)
        public List<JobApplicationResponse> getMyApplications(
                        Long candidateUserId) {

                return jobApplicationRepository
                                .findAllByCandidateUserIdOrderByAppliedAtDesc(candidateUserId)
                                .stream()
                                .map(this::toResponse)
                                .toList();
        }

        @Transactional(readOnly = true)
        public JobApplicationInternalResponse getInternalById(
                        Long applicationId) {

                JobApplication application = getEntity(applicationId);

                return new JobApplicationInternalResponse(
                                application.getId(),
                                application.getCandidateUserId(),
                                application.getJobOfferId(),
                                application.getStatus());
        }

        @Transactional(readOnly = true)
        public List<JobApplicationResponse> getReceivedApplications(
                        Long jobOfferId,
                        Long recruiterUserId) {

                jobServiceClient.verifyRecruiterOwnsOffer(
                                jobOfferId, recruiterUserId);

                return jobApplicationRepository
                                .findAllByJobOfferIdOrderByAppliedAtDesc(jobOfferId)
                                .stream()
                                .map(this::toResponse)
                                .toList();
        }

        public JobApplicationResponse update(
                        Long applicationId,
                        Long candidateUserId,
                        UpdateJobApplicationRequest request) {

                JobApplication application = getEntity(applicationId);
                verifyCandidateOwnership(application, candidateUserId);

                if (application.getStatus() != ApplicationStatus.PENDING) {
                        throw new IllegalArgumentException(
                                        "Seule une candidature PENDING peut être modifiée.");
                }
                Resume resume = getOwnedResume(
                                candidateUserId,
                                request.resumeId());

                application.setResumeId(resume.getId());
                application.setCoverLetter(normalize(request.coverLetter()));

                return toResponse(jobApplicationRepository.save(application));
        }

        public void delete(
                        Long applicationId,
                        Long candidateUserId) {

                JobApplication application = getEntity(applicationId);
                verifyCandidateOwnership(application, candidateUserId);

                if (application.getStatus() != ApplicationStatus.PENDING) {
                        throw new IllegalArgumentException(
                                        "Seule une candidature PENDING peut être supprimée.");
                }

                jobApplicationRepository.delete(application);
        }

        public JobApplicationResponse updateStatus(
                        Long applicationId,
                        Long recruiterUserId,
                        UpdateApplicationStatusRequest request) {

                JobApplication application = getEntity(applicationId);

                jobServiceClient.verifyRecruiterOwnsOffer(
                                application.getJobOfferId(), recruiterUserId);

                ApplicationStatus currentStatus = application.getStatus();
                ApplicationStatus newStatus = request.status();

                if (!isTransitionAllowed(currentStatus, newStatus)) {
                        throw new IllegalArgumentException(
                                        "Transition non autorisée : "
                                                        + currentStatus + " -> " + newStatus);
                }

                application.setStatus(newStatus);

                JobApplication savedApplication = jobApplicationRepository.save(application);

                ApplicationStatusHistory history = new ApplicationStatusHistory();

                history.setJobApplicationId(savedApplication.getId());
                history.setPreviousStatus(currentStatus);
                history.setNewStatus(newStatus);
                history.setChangedByRecruiterUserId(recruiterUserId);

                applicationStatusHistoryRepository.save(history);

                return toResponse(savedApplication);
        }

        @Transactional(readOnly = true)
        public List<ApplicationStatusHistoryResponse> getStatusHistory(
                        Long applicationId,
                        Long userId,
                        String role) {

                JobApplication application = getEntity(applicationId);

                boolean isCandidateOwner = application.getCandidateUserId().equals(userId);

                boolean isRecruiterOwner = false;

                if ("RECRUITER".equals(role)) {
                        try {
                                jobServiceClient.verifyRecruiterOwnsOffer(
                                                application.getJobOfferId(),
                                                userId);

                                isRecruiterOwner = true;

                        } catch (IllegalArgumentException exception) {
                                isRecruiterOwner = false;
                        }
                }

                if (!isCandidateOwner && !isRecruiterOwner) {
                        throw new AccessDeniedException(
                                        "Vous ne pouvez pas consulter cet historique.");
                }

                return applicationStatusHistoryRepository
                                .findAllByJobApplicationIdOrderByChangedAtDesc(
                                                applicationId)
                                .stream()
                                .map(this::toHistoryResponse)
                                .toList();
        }

        private ApplicationStatusHistoryResponse toHistoryResponse(
                        ApplicationStatusHistory history) {

                return new ApplicationStatusHistoryResponse(
                                history.getId(),
                                history.getPreviousStatus(),
                                history.getNewStatus(),
                                history.getChangedByRecruiterUserId(),
                                history.getChangedAt());
        }

        private JobApplication getEntity(Long applicationId) {
                return jobApplicationRepository.findById(applicationId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Candidature introuvable."));
        }

        private void verifyCandidateOwnership(
                        JobApplication application,
                        Long candidateUserId) {

                if (!application.getCandidateUserId().equals(candidateUserId)) {
                        throw new AccessDeniedException(
                                        "Vous ne gérez pas cette candidature.");
                }
        }

        private boolean isTransitionAllowed(
                        ApplicationStatus currentStatus,
                        ApplicationStatus newStatus) {

                return switch (currentStatus) {
                        case PENDING -> newStatus == ApplicationStatus.REVIEWING
                                        || newStatus == ApplicationStatus.REJECTED;

                        case REVIEWING -> newStatus == ApplicationStatus.INTERVIEW
                                        || newStatus == ApplicationStatus.ACCEPTED
                                        || newStatus == ApplicationStatus.REJECTED;

                        case INTERVIEW -> newStatus == ApplicationStatus.ACCEPTED
                                        || newStatus == ApplicationStatus.REJECTED;

                        case ACCEPTED, REJECTED -> false;
                };
        }

        private String normalize(String value) {
                return value == null || value.isBlank() ? null : value.trim();
        }

        private Resume getOwnedResume(
                        Long candidateUserId,
                        Long resumeId) {

                Resume resume = resumeRepository.findById(resumeId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "CV introuvable."));

                if (!resume.getCandidateUserId().equals(candidateUserId)) {
                        throw new AccessDeniedException(
                                        "Ce CV ne vous appartient pas.");
                }

                return resume;
        }

        private JobApplicationResponse toResponse(
                        JobApplication application) {

                return new JobApplicationResponse(
                                application.getId(),
                                application.getCandidateUserId(),
                                application.getJobOfferId(),
                                application.getResumeId(),
                                application.getStatus(),
                                application.getCoverLetter(),
                                application.getAppliedAt());

        }
}