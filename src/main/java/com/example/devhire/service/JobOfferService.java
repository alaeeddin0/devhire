package com.example.devhire.service;

import com.example.devhire.dto.jobOffer.JobOfferResponse;
import com.example.devhire.dto.jobOffer.UpdateJobOfferRequest;
import com.example.devhire.exception.ResourceNotFoundException;
import com.example.devhire.model.JobOffer;
import com.example.devhire.model.RecruiterProfile;
import com.example.devhire.model.UserRole;
import com.example.devhire.repo.JobOfferRepository;
import com.example.devhire.repo.RecruiterProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JobOfferService {

    private final JobOfferRepository jobOfferRepository;
    private final RecruiterProfileRepository recruiterProfileRepository;

    public List<JobOfferResponse> getAllJobOffers() {
        return jobOfferRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public JobOfferResponse getJobOfferById(Long id) {
        return toResponse(getJobOfferEntityById(id));
    }

    @Transactional
    public JobOfferResponse createJobOffer(
            JobOffer jobOffer,
            Long recruiterProfileId) {
        RecruiterProfile recruiter = recruiterProfileRepository
                .findById(recruiterProfileId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Recruteur introuvable avec l'id : "
                                + recruiterProfileId));

        if (recruiter.getUser().getRole() != UserRole.RECRUITER) {
            throw new IllegalStateException(
                    "Seul un recruteur peut créer une offre.");
        }

        jobOffer.setId(null);
        jobOffer.setRecruiter(recruiter);

        return toResponse(jobOfferRepository.save(jobOffer));
    }

    @Transactional
    public void deleteJobOffer(
            Long jobOfferId,
            Long recruiterProfileId) {
        JobOffer jobOffer = getJobOfferEntityById(jobOfferId);

        RecruiterProfile recruiter = recruiterProfileRepository
                .findById(recruiterProfileId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Profil recruteur introuvable avec l'id : "
                                + recruiterProfileId));

        if (!jobOffer.getRecruiter().getId().equals(recruiter.getId())) {
            throw new IllegalArgumentException(
                    "Ce recruteur ne peut pas supprimer cette offre.");
        }

        jobOfferRepository.delete(jobOffer);
    }

    private JobOffer getJobOfferEntityById(Long id) {
        return jobOfferRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Offre introuvable avec l'id : " + id));
    }

    private JobOfferResponse toResponse(JobOffer jobOffer) {
        return new JobOfferResponse(
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
    }

    @Transactional(readOnly = true)
    public Page<JobOfferResponse> searchJobOffers(
            String keyword,
            String location,
            String workMode,
            String offerType,
            int page,
            int size) {
        if (page < 0) {
            throw new IllegalArgumentException(
                    "Le numéro de page ne peut pas être négatif.");
        }

        if (size < 1 || size > 50) {
            throw new IllegalArgumentException(
                    "La taille de page doit être comprise entre 1 et 50.");
        }

        PageRequest pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "createdAt"));

        return jobOfferRepository.search(
                normalize(keyword),
                normalize(location),
                normalize(workMode),
                normalize(offerType),
                pageable).map(this::toResponse);
    }

    private String normalize(String value) {
        return (value == null || value.isBlank()) ? null : value.trim();
    }

    @Transactional
    public JobOfferResponse updateJobOffer(
            Long jobOfferId,
            Long recruiterProfileId,
            UpdateJobOfferRequest request) {
        JobOffer jobOffer = getJobOfferEntityById(jobOfferId);

        RecruiterProfile recruiter = recruiterProfileRepository
                .findById(recruiterProfileId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Profil recruteur introuvable avec l'id : "
                                + recruiterProfileId));

        if (!jobOffer.getRecruiter().getId().equals(recruiter.getId())) {
            throw new IllegalArgumentException(
                    "Ce recruteur ne peut pas modifier cette offre.");
        }

        if (request.salaryMax().compareTo(request.salaryMin()) < 0) {
            throw new IllegalArgumentException(
                    "Le salaire maximum doit être supérieur ou égal au salaire minimum.");
        }

        jobOffer.setTitle(request.title());
        jobOffer.setDescription(request.description());
        jobOffer.setCompany(request.company());
        jobOffer.setLocation(request.location());
        jobOffer.setWorkMode(request.workMode());
        jobOffer.setOfferType(request.offerType());
        jobOffer.setSalaryMin(request.salaryMin());
        jobOffer.setSalaryMax(request.salaryMax());

        return toResponse(jobOfferRepository.save(jobOffer));
    }
}