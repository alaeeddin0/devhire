package com.example.devhire.interview.config;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app.security.internal-api")
public record InternalApiProperties(

        @NotBlank String key) {
}