package com.example.devhire.auth.service;

import com.example.devhire.auth.dto.auth.AuthResponse;
import com.example.devhire.auth.dto.auth.LoginRequest;
import com.example.devhire.auth.model.User;
import com.example.devhire.auth.repo.UserRepository;
import com.example.devhire.auth.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public AuthResponse login(LoginRequest request) {
        String email = request.email().trim().toLowerCase(Locale.ROOT);

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            email,
                            request.password()));
        } catch (Exception exception) {
            throw new BadCredentialsException(
                    "Email ou mot de passe incorrect.");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadCredentialsException(
                        "Email ou mot de passe incorrect."));

        return new AuthResponse(
                jwtService.generateToken(user),
                "Bearer",
                user.getId(),
                user.getEmail(),
                user.getRole());
    }
}