package com.example.devhire.auth.repo;

import com.example.devhire.auth.model.CandidateProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CandidateProfileRepository
        extends JpaRepository<CandidateProfile, Long> {

    boolean existsByUserId(Long userId);

    Optional<CandidateProfile> findByUserEmail(String email);
}