package com.example.devhire.application.client;

import com.example.devhire.application.exception.ResourceNotFoundException;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

@Component
public class JobServiceClient {

    private final RestClient restClient;

    public JobServiceClient(
            @LoadBalanced RestClient.Builder restClientBuilder) {

        this.restClient = restClientBuilder
                .baseUrl("http://JOB-SERVICE")
                .build();
    }

    public JobOfferRemoteResponse getOffer(Long jobOfferId) {
        try {
            return restClient.get()
                    .uri("/api/job-offers/{id}", jobOfferId)
                    .retrieve()
                    .body(JobOfferRemoteResponse.class);

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