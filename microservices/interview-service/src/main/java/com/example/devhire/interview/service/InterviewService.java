package com.example.devhire.interview.service;

import com.example.devhire.interview.client.ApplicationServiceClient;
import com.example.devhire.interview.client.JobApplicationRemoteResponse;
import com.example.devhire.interview.client.JobServiceClient;
import com.example.devhire.interview.dto.CreateInterviewRequest;
import com.example.devhire.interview.dto.InterviewResponse;
import com.example.devhire.interview.dto.UpdateInterviewRequest;
import com.example.devhire.interview.exception.ResourceNotFoundException;
import com.example.devhire.interview.model.Interview;
import com.example.devhire.interview.model.InterviewStatus;
import com.example.devhire.interview.model.InterviewType;
import com.example.devhire.interview.repo.InterviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class InterviewService {

    private final InterviewRepository interviewRepository;
    private final ApplicationServiceClient applicationServiceClient;
    private final JobServiceClient jobServiceClient;

    public InterviewResponse create(
            Long recruiterUserId,
            CreateInterviewRequest request) {

        JobApplicationRemoteResponse application = getAndVerifyApplication(
                request.jobApplicationId(),
                recruiterUserId);

        validateMeetingDetails(
                request.type(),
                request.meetingLink(),
                request.location());

        Interview interview = new Interview();
        interview.setJobApplicationId(application.id());
        interview.setCandidateUserId(application.candidateUserId());
        interview.setRecruiterUserId(recruiterUserId);
        interview.setType(request.type());
        interview.setScheduledAt(request.scheduledAt());
        interview.setDurationMinutes(request.durationMinutes());
        interview.setMeetingLink(normalize(request.meetingLink()));
        interview.setLocation(normalize(request.location()));
        interview.setNotes(normalize(request.notes()));

        return toResponse(interviewRepository.save(interview));
    }

    @Transactional(readOnly = true)
    public List<InterviewResponse> getMyCandidateInterviews(
            Long candidateUserId) {

        return interviewRepository
                .findAllByCandidateUserIdOrderByScheduledAtAsc(
                        candidateUserId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<InterviewResponse> getMyRecruiterInterviews(
            Long recruiterUserId) {

        return interviewRepository
                .findAllByRecruiterUserIdOrderByScheduledAtAsc(
                        recruiterUserId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public InterviewResponse update(
            Long interviewId,
            Long recruiterUserId,
            UpdateInterviewRequest request) {

        Interview interview = getOwnedInterview(
                interviewId, recruiterUserId);

        if (interview.getStatus() != InterviewStatus.SCHEDULED) {
            throw new IllegalArgumentException(
                    "Seul un entretien planifié peut être modifié.");
        }

        validateMeetingDetails(
                request.type(),
                request.meetingLink(),
                request.location());

        interview.setType(request.type());
        interview.setScheduledAt(request.scheduledAt());
        interview.setDurationMinutes(request.durationMinutes());
        interview.setMeetingLink(normalize(request.meetingLink()));
        interview.setLocation(normalize(request.location()));
        interview.setNotes(normalize(request.notes()));

        return toResponse(interviewRepository.save(interview));
    }

    public InterviewResponse cancel(
            Long interviewId,
            Long recruiterUserId) {

        Interview interview = getOwnedInterview(
                interviewId, recruiterUserId);

        if (interview.getStatus() != InterviewStatus.SCHEDULED) {
            throw new IllegalArgumentException(
                    "Seul un entretien planifié peut être annulé.");
        }

        interview.setStatus(InterviewStatus.CANCELLED);

        return toResponse(interviewRepository.save(interview));
    }

    public InterviewResponse complete(
            Long interviewId,
            Long recruiterUserId) {

        Interview interview = getOwnedInterview(
                interviewId, recruiterUserId);

        if (interview.getStatus() != InterviewStatus.SCHEDULED) {
            throw new IllegalArgumentException(
                    "Seul un entretien planifié peut être terminé.");
        }

        interview.setStatus(InterviewStatus.COMPLETED);

        return toResponse(interviewRepository.save(interview));
    }

    private JobApplicationRemoteResponse getAndVerifyApplication(
            Long applicationId,
            Long recruiterUserId) {

        JobApplicationRemoteResponse application = applicationServiceClient.getApplication(applicationId);

        jobServiceClient.verifyRecruiterOwnsOffer(
                application.jobOfferId(),
                recruiterUserId);

        if (!"INTERVIEW".equals(application.status())) {
            throw new IllegalArgumentException(
                    "La candidature doit être au statut INTERVIEW.");
        }

        return application;
    }

    private Interview getOwnedInterview(
            Long interviewId,
            Long recruiterUserId) {

        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Entretien introuvable."));

        if (!interview.getRecruiterUserId()
                .equals(recruiterUserId)) {
            throw new AccessDeniedException(
                    "Vous ne gérez pas cet entretien.");
        }

        return interview;
    }

    private void validateMeetingDetails(
            InterviewType type,
            String meetingLink,
            String location) {

        if (type == InterviewType.ONLINE
                && (meetingLink == null || meetingLink.isBlank())) {
            throw new IllegalArgumentException(
                    "Un lien est obligatoire pour un entretien en ligne.");
        }

        if (type == InterviewType.ON_SITE
                && (location == null || location.isBlank())) {
            throw new IllegalArgumentException(
                    "Un lieu est obligatoire pour un entretien sur site.");
        }
    }

    private String normalize(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private InterviewResponse toResponse(Interview interview) {
        return new InterviewResponse(
                interview.getId(),
                interview.getJobApplicationId(),
                interview.getType(),
                interview.getStatus(),
                interview.getScheduledAt(),
                interview.getDurationMinutes(),
                interview.getMeetingLink(),
                interview.getLocation(),
                interview.getNotes(),
                interview.getCreatedAt());
    }
}