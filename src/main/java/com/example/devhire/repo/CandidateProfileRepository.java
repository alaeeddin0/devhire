package com.example.devhire.repo;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.devhire.model.CandidateProfile;

public interface CandidateProfileRepository extends JpaRepository<CandidateProfile, Long> {
    boolean existsByUserId(Long userId);
    
    Optional<CandidateProfile> findByUserEmail(String email);
}