package com.example.devhire.interview.client;

import com.example.devhire.interview.exception.ResourceNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Component
public class JobServiceClient {

    private final RestTemplate restTemplate;

    public JobServiceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void verifyRecruiterOwnsOffer(
            Long jobOfferId,
            Long recruiterUserId) {

        try {
            JobOfferRemoteResponse offer = restTemplate.getForObject(
                    "http://JOB-SERVICE/api/job-offers/{id}",
                    JobOfferRemoteResponse.class,
                    jobOfferId);

            if (offer == null
                    || !offer.recruiterUserId().equals(recruiterUserId)) {
                throw new IllegalArgumentException(
                        "Vous ne gérez pas cette offre.");
            }

        } catch (HttpClientErrorException.NotFound exception) {
            throw new ResourceNotFoundException("Offre introuvable.");
        }
    }
}