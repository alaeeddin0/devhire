package com.example.devhire.application.client;

import com.example.devhire.application.exception.ResourceNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientResponseException;

@Component
public class JobServiceClient {

    private final RestTemplate restTemplate;

    public JobServiceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public JobOfferRemoteResponse getOffer(Long jobOfferId) {
        try {
            return restTemplate.getForObject(
                    "http://JOB-SERVICE/api/job-offers/{id}",
                    JobOfferRemoteResponse.class,
                    jobOfferId);

        } catch (RestClientResponseException exception) {
            if (exception.getStatusCode().value() == 404) {
                throw new ResourceNotFoundException(
                        "Offre introuvable.");
            }

            throw exception;
        }
    }

    public void verifyRecruiterOwnsOffer(
            Long jobOfferId,
            Long recruiterUserId) {

        JobOfferRemoteResponse offer = getOffer(jobOfferId);

        if (!offer.recruiterUserId().equals(recruiterUserId)) {
            throw new IllegalArgumentException(
                    "Vous ne gérez pas cette offre.");
        }
    }
}