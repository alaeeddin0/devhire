package com.example.devhire.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.devhire.model.CandidateProfile;

public interface CandidateProfileRepository extends JpaRepository<CandidateProfile, Long> {
    boolean existsByUserId(Long userId);
}