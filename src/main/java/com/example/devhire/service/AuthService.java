package com.example.devhire.service;

import com.example.devhire.config.JwtProperties;
import com.example.devhire.dto.auth.AuthResponse;
import com.example.devhire.dto.auth.LoginRequest;
import com.example.devhire.exception.ResourceNotFoundException;
import com.example.devhire.model.User;
import com.example.devhire.repo.UserRepository;
import com.example.devhire.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final JwtProperties jwtProperties;

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        String cleanEmail = request.email().trim().toLowerCase();
        authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated(
                        cleanEmail,
                        request.password()));

        User user = userRepository.findByEmail(cleanEmail)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Utilisateur introuvable."));

        if (!user.isActive()) {
            // Même message que pour un mauvais mot de passe :
            // ne pas révéler l’état d’un compte à un attaquant.
            throw new BadCredentialsException(
                    "Email ou mot de passe invalide.");
        }

        String token = jwtService.generateToken(user);

        return new AuthResponse(
                token,
                "Bearer",
                jwtProperties.expirationMs() / 1000,
                user.getId(),
                user.getEmail(),
                user.getRole());
    }
}