package com.example.devhire.application.security;

import com.example.devhire.application.config.InternalApiProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;

@Component
@RequiredArgsConstructor
public class InternalApiKeyFilter extends OncePerRequestFilter {

    private static final String INTERNAL_PATH = "/api/job-applications/internal/";

    private final InternalApiProperties internalApiProperties;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getRequestURI().startsWith(INTERNAL_PATH);
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String receivedKey = request.getHeader("X-Internal-Api-Key");

        boolean keyIsValid = receivedKey != null
                && MessageDigest.isEqual(
                        receivedKey.getBytes(StandardCharsets.UTF_8),
                        internalApiProperties.key()
                                .getBytes(StandardCharsets.UTF_8));

        if (!keyIsValid) {
            response.sendError(
                    HttpStatus.FORBIDDEN.value(),
                    "Accès interne refusé.");
            return;
        }

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                "interview-service",
                null,
                List.of(new SimpleGrantedAuthority(
                        "ROLE_INTERNAL_SERVICE")));

        SecurityContextHolder.getContext()
                .setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }
}