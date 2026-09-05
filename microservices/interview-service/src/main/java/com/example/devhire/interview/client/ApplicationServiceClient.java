package com.example.devhire.interview.client;

import com.example.devhire.interview.config.InternalApiProperties;
import com.example.devhire.interview.exception.ResourceNotFoundException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Component
public class ApplicationServiceClient {

    private final RestTemplate restTemplate;
    private final InternalApiProperties internalApiProperties;

    public ApplicationServiceClient(
            RestTemplate restTemplate,
            InternalApiProperties internalApiProperties) {

        this.restTemplate = restTemplate;
        this.internalApiProperties = internalApiProperties;
    }

    public JobApplicationRemoteResponse getApplication(Long applicationId) {

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set(
                    "X-Internal-Api-Key",
                    internalApiProperties.key());

            HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

            ResponseEntity<JobApplicationRemoteResponse> response = restTemplate.exchange(
                    "http://APPLICATION-SERVICE"
                            + "/api/job-applications/internal/{id}",
                    HttpMethod.GET,
                    requestEntity,
                    JobApplicationRemoteResponse.class,
                    applicationId);

            return response.getBody();

        } catch (HttpClientErrorException.NotFound exception) {
            throw new ResourceNotFoundException("Candidature introuvable.");
        }
    }
}