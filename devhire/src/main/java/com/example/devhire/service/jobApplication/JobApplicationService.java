package com.example.devhire.service.jobApplication;

import com.example.devhire.dto.jobApplication.ApplicationStatusHistoryResponse;
import com.example.devhire.dto.jobApplication.CreateJobApplicationRequest;
import com.example.devhire.dto.jobApplication.JobApplicationResponse;
import com.example.devhire.dto.jobApplication.UpdateApplicationStatusRequest;
import com.example.devhire.exception.ResourceNotFoundException;
import com.example.devhire.model.candidate.CandidateProfile;
import com.example.devhire.model.jobApplication.ApplicationStatus;
import com.example.devhire.model.jobApplication.JobApplication;
import com.example.devhire.model.jobOffer.JobOffer;
import com.example.devhire.model.recruiter.RecruiterProfile;
import com.example.devhire.model.resume.Resume;
import com.example.devhire.repo.candidate.CandidateProfileRepository;
import com.example.devhire.repo.jobApplication.JobApplicationRepository;
import com.example.devhire.repo.jobOffer.JobOfferRepository;
import com.example.devhire.repo.recruiter.RecruiterProfileRepository;
import com.example.devhire.repo.resume.ResumeRepository;
import com.example.devhire.model.jobApplication.ApplicationStatusHistory;
import com.example.devhire.repo.jobApplication.ApplicationStatusHistoryRepository;
import lombok.RequiredArgsConstructor;
import com.example.devhire.dto.jobApplication.UpdateJobApplicationRequest;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.AccessDeniedException;

@Service
@RequiredArgsConstructor
public class JobApplicationService {

        private final JobApplicationRepository jobApplicationRepository;
        private final CandidateProfileRepository candidateProfileRepository;
        private final JobOfferRepository jobOfferRepository;
        private final ResumeRepository resumeRepository;
        private final RecruiterProfileRepository recruiterProfileRepository;
        private final ApplicationStatusHistoryRepository applicationStatusHistoryRepository;

        private CandidateProfile getCandidateByEmail(String email) {
                return candidateProfileRepository.findByUserEmail(email)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Profil candidat introuvable."));
        }

        private RecruiterProfile getRecruiterByEmail(String email) {
                return recruiterProfileRepository.findByUserEmail(email)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Profil recruteur introuvable."));
        }

        public List<JobApplicationResponse> getReceivedApplications(
                        String email) {
                RecruiterProfile recruiter = getRecruiterByEmail(email);

                return jobApplicationRepository
                                .findAllByJobOfferRecruiterIdOrderByAppliedAtDesc(
                                                recruiter.getId())
                                .stream()
                                .map(this::toResponse)
                                .toList();
        }

        private JobApplication getApplicationEntityById(Long id) {
                return jobApplicationRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Candidature introuvable avec l'id : " + id));
        }

        public List<JobApplicationResponse> getMyApplications(String email) {
                CandidateProfile candidate = getCandidateByEmail(email);

                return jobApplicationRepository
                                .findAllByCandidateIdOrderByAppliedAtDesc(candidate.getId())
                                .stream()
                                .map(this::toResponse)
                                .toList();
        }

        public JobApplicationResponse getApplicationById(
                        Long applicationId,
                        String email) {
                JobApplication application = getApplicationEntityById(applicationId);

                boolean isCandidateOwner = candidateProfileRepository
                                .findByUserEmail(email)
                                .map(candidate -> candidate.getId().equals(
                                                application.getCandidate().getId()))
                                .orElse(false);

                boolean isRecruiterOwner = recruiterProfileRepository
                                .findByUserEmail(email)
                                .map(recruiter -> recruiter.getId().equals(
                                                application.getJobOffer()
                                                                .getRecruiter()
                                                                .getId()))
                                .orElse(false);

                if (!isCandidateOwner && !isRecruiterOwner) {
                        throw new AccessDeniedException(
                                        "Vous ne pouvez pas consulter cette candidature.");
                }

                return toResponse(application);
        }

        @Transactional
        public JobApplicationResponse createApplication(
                        String email,
                        CreateJobApplicationRequest request) {
                CandidateProfile candidate = getCandidateByEmail(email);

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
                        String email,
                        UpdateJobApplicationRequest request) {
                JobApplication application = getApplicationEntityById(applicationId);

                CandidateProfile candidate = getCandidateByEmail(email);

                if (!application.getCandidate().getId().equals(candidate.getId())) {
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

                if (!resume.getCandidate().getId().equals(candidate.getId())) {
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
                        String email) {
                JobApplication application = getApplicationEntityById(applicationId);

                CandidateProfile candidate = getCandidateByEmail(email);

                if (!application.getCandidate().getId().equals(candidate.getId())) {
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
                        String email,
                        UpdateApplicationStatusRequest request) {
                JobApplication application = jobApplicationRepository
                                .findById(applicationId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Candidature introuvable avec l'id : " + applicationId));

                RecruiterProfile recruiter = getRecruiterByEmail(email);

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

                JobApplication savedApplication = jobApplicationRepository.save(application);

                ApplicationStatusHistory history = ApplicationStatusHistory.builder()
                                .jobApplication(savedApplication)
                                .previousStatus(currentStatus)
                                .newStatus(newStatus)
                                .changedByRecruiter(recruiter)
                                .build();

                applicationStatusHistoryRepository.save(history);

                return toResponse(savedApplication);
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

        @Transactional(readOnly = true)
        public List<ApplicationStatusHistoryResponse> getApplicationStatusHistory(
                        Long applicationId,
                        String email) {

                JobApplication application = getApplicationEntityById(applicationId);

                boolean isCandidateOwner = candidateProfileRepository
                                .findByUserEmail(email)
                                .map(candidate -> candidate.getId().equals(
                                                application.getCandidate().getId()))
                                .orElse(false);

                boolean isRecruiterOwner = recruiterProfileRepository
                                .findByUserEmail(email)
                                .map(recruiter -> recruiter.getId().equals(
                                                application.getJobOffer().getRecruiter().getId()))
                                .orElse(false);

                if (!isCandidateOwner && !isRecruiterOwner) {
                        throw new AccessDeniedException(
                                        "Vous ne pouvez pas consulter l'historique de cette candidature.");
                }

                return applicationStatusHistoryRepository
                                .findAllByJobApplicationIdOrderByChangedAtDesc(applicationId)
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
                                history.getChangedByRecruiter().getId(),
                                history.getChangedAt());
        }
}
