package com.example.devhire.repo.recruiter;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.devhire.model.recruiter.RecruiterProfile;

public interface RecruiterProfileRepository extends JpaRepository<RecruiterProfile, Long> {
    boolean existsByUserId(Long userId);
    
    Optional<RecruiterProfile> findByUserEmail(String email);
}
