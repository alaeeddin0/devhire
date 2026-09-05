package com.example.devhire.interview.security;

import com.example.devhire.interview.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtProperties jwtProperties;

    public AuthenticatedUser extractUser(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        Number userId = claims.get("userId", Number.class);
        String email = claims.getSubject();
        String role = claims.get("role", String.class);

        if (userId == null || email == null || role == null) {
            throw new IllegalArgumentException("Token JWT incomplet.");
        }

        return new AuthenticatedUser(
                userId.longValue(),
                email,
                role);
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(
                jwtProperties.secret());

        return Keys.hmacShaKeyFor(keyBytes);
    }
}