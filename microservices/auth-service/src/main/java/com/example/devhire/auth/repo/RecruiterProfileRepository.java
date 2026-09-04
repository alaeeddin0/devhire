package com.example.devhire.auth.repo;

import com.example.devhire.auth.model.RecruiterProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RecruiterProfileRepository
        extends JpaRepository<RecruiterProfile, Long> {

    boolean existsByUserId(Long userId);

    Optional<RecruiterProfile> findByUserEmail(String email);
}