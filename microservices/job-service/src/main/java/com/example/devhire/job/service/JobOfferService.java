package com.example.devhire.job.service;

import com.example.devhire.job.dto.JobOfferRequest;
import com.example.devhire.job.dto.JobOfferResponse;
import com.example.devhire.job.exception.ResourceNotFoundException;
import com.example.devhire.job.model.JobOffer;
import com.example.devhire.job.repo.JobOfferRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class JobOfferService {

    private final JobOfferRepository jobOfferRepository;

    @Transactional(readOnly = true)
    public Page<JobOfferResponse> search(
            String keyword,
            String location,
            String workMode,
            String offerType,
            int page,
            int size) {

        if (page < 0) {
            throw new IllegalArgumentException("La page ne peut pas être négative.");
        }

        if (size < 1 || size > 50) {
            throw new IllegalArgumentException(
                    "La taille doit être comprise entre 1 et 50.");
        }

        PageRequest pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "createdAt"));

        return jobOfferRepository.search(
                normalizeNullable(keyword),
                normalizeNullable(location),
                normalizeNullable(workMode),
                normalizeNullable(offerType),
                pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public JobOfferResponse getById(Long id) {
        return toResponse(getEntity(id));
    }

    public JobOfferResponse create(
            Long recruiterUserId,
            JobOfferRequest request) {

        validateSalary(request.salaryMin(), request.salaryMax());

        JobOffer offer = new JobOffer();
        offer.setRecruiterUserId(recruiterUserId);
        apply(offer, request);

        return toResponse(jobOfferRepository.save(offer));
    }

    public JobOfferResponse update(
            Long id,
            Long recruiterUserId,
            JobOfferRequest request) {

        validateSalary(request.salaryMin(), request.salaryMax());

        JobOffer offer = getEntity(id);
        verifyOwner(offer, recruiterUserId);
        apply(offer, request);

        return toResponse(jobOfferRepository.save(offer));
    }

    public void delete(Long id, Long recruiterUserId) {
        JobOffer offer = getEntity(id);
        verifyOwner(offer, recruiterUserId);
        jobOfferRepository.delete(offer);
    }

    private JobOffer getEntity(Long id) {
        return jobOfferRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Offre introuvable."));
    }

    private void verifyOwner(
            JobOffer offer,
            Long recruiterUserId) {

        if (!offer.getRecruiterUserId().equals(recruiterUserId)) {
            throw new IllegalArgumentException(
                    "Vous ne pouvez pas modifier cette offre.");
        }
    }

    private void apply(
            JobOffer offer,
            JobOfferRequest request) {

        offer.setTitle(request.title().trim());
        offer.setDescription(request.description().trim());
        offer.setCompany(request.company().trim());
        offer.setLocation(request.location().trim());
        offer.setWorkMode(request.workMode().trim().toUpperCase());
        offer.setOfferType(request.offerType().trim().toUpperCase());
        offer.setSalaryMin(request.salaryMin());
        offer.setSalaryMax(request.salaryMax());
    }

    private void validateSalary(
            java.math.BigDecimal salaryMin,
            java.math.BigDecimal salaryMax) {

        if (salaryMin != null && salaryMax != null
                && salaryMax.compareTo(salaryMin) < 0) {
            throw new IllegalArgumentException(
                    "Le salaire maximum doit être supérieur ou égal au minimum.");
        }
    }

    private String normalizeNullable(String value) {
        return value == null || value.isBlank()
                ? null
                : value.trim();
    }

    private JobOfferResponse toResponse(JobOffer offer) {
        return new JobOfferResponse(
                offer.getId(),
                offer.getTitle(),
                offer.getDescription(),
                offer.getCompany(),
                offer.getLocation(),
                offer.getWorkMode(),
                offer.getOfferType(),
                offer.getSalaryMin(),
                offer.getSalaryMax(),
                offer.getCreatedAt(),
                offer.getRecruiterUserId());
    }
}