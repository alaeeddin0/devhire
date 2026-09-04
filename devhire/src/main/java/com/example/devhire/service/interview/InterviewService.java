package com.example.devhire.service.interview;

import com.example.devhire.dto.interview.CreateInterviewRequest;
import com.example.devhire.dto.interview.InterviewResponse;
import com.example.devhire.exception.ResourceNotFoundException;
import com.example.devhire.model.interview.Interview;
import com.example.devhire.model.interview.InterviewStatus;
import com.example.devhire.model.interview.InterviewType;
import com.example.devhire.model.jobApplication.ApplicationStatus;
import com.example.devhire.model.jobApplication.JobApplication;
import com.example.devhire.repo.interview.InterviewRepository;
import com.example.devhire.repo.jobApplication.JobApplicationRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import com.example.devhire.dto.interview.UpdateInterviewRequest;

@Service
@RequiredArgsConstructor
@Transactional
public class InterviewService {

    private final InterviewRepository interviewRepository;
    private final JobApplicationRepository jobApplicationRepository;

    @Transactional
    public InterviewResponse createInterview(
            String email,
            CreateInterviewRequest request) {

        JobApplication application = getJobApplicationById(
                request.jobApplicationId());

        verifyRecruiterOwnership(application, email);

        if (application.getStatus() != ApplicationStatus.INTERVIEW) {
            throw new IllegalArgumentException(
                    "La candidature doit avoir le statut INTERVIEW "
                            + "avant de planifier un entretien.");
        }

        validateMeetingDetails(
                request.type(),
                request.meetingLink(),
                request.location());

        Interview interview = Interview.builder()
                .jobApplication(application)
                .type(request.type())
                .scheduledAt(request.scheduledAt())
                .durationMinutes(request.durationMinutes())
                .meetingLink(normalize(request.meetingLink()))
                .location(normalize(request.location()))
                .notes(normalize(request.notes()))
                .build();

        return toResponse(interviewRepository.save(interview));
    }

    @Transactional(readOnly = true)
    public List<InterviewResponse> getMyCandidateInterviews(String email) {
        return interviewRepository
                .findAllByJobApplicationCandidateUserEmailOrderByScheduledAtAsc(
                        email)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<InterviewResponse> getMyRecruiterInterviews(String email) {
        return interviewRepository
                .findAllByJobApplicationJobOfferRecruiterUserEmailOrderByScheduledAtAsc(
                        email)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public InterviewResponse updateInterview(
            Long interviewId,
            String email,
            UpdateInterviewRequest request) {

        Interview interview = getInterviewById(interviewId);

        verifyRecruiterOwnership(interview.getJobApplication(), email);

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

    public InterviewResponse cancelInterview(
            Long interviewId,
                    String email) {

            Interview interview = getInterviewById(interviewId);

            verifyRecruiterOwnership(interview.getJobApplication(), email);

            if (interview.getStatus() != InterviewStatus.SCHEDULED) {
                    throw new IllegalArgumentException(
                                    "Seul un entretien planifié peut être annulé.");
            }

            interview.setStatus(InterviewStatus.CANCELLED);

            return toResponse(interviewRepository.save(interview));
    }
    
    public InterviewResponse completeInterview(
                    Long interviewId,
                    String email) {

            Interview interview = getInterviewById(interviewId);

            verifyRecruiterOwnership(interview.getJobApplication(), email);

            if (interview.getStatus() != InterviewStatus.SCHEDULED) {
                    throw new IllegalArgumentException(
                                    "Seul un entretien planifié peut être marqué comme terminé.");
            }

            interview.setStatus(InterviewStatus.COMPLETED);

            return toResponse(interviewRepository.save(interview));
    }

    private Interview getInterviewById(Long interviewId) {
        return interviewRepository.findById(interviewId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Entretien introuvable."));
    }
    
    private JobApplication getJobApplicationById(Long id) {
        return jobApplicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Candidature introuvable."));
    }

    private void verifyRecruiterOwnership(
            JobApplication application,
            String email) {

        String recruiterEmail = application.getJobOffer()
                .getRecruiter()
                .getUser()
                .getEmail();

        if (!recruiterEmail.equalsIgnoreCase(email)) {
            throw new AccessDeniedException(
                    "Vous ne pouvez pas gérer les entretiens de cette candidature.");
        }
    }

    private void validateMeetingDetails(
            InterviewType type,
            String meetingLink,
            String location) {

        if (type == InterviewType.ONLINE
                && (meetingLink == null || meetingLink.isBlank())) {
            throw new IllegalArgumentException(
                    "Un lien de réunion est obligatoire pour un entretien en ligne.");
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
                interview.getJobApplication().getId(),
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
