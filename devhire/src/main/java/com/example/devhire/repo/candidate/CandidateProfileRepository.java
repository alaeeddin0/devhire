package com.example.devhire.repo.candidate;

import com.example.devhire.model.candidate.CandidateProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CandidateProfileRepository
        extends JpaRepository<CandidateProfile, Long> {

    boolean existsByUserId(Long userId);

    Optional<CandidateProfile> findByUserEmail(String email);
}
