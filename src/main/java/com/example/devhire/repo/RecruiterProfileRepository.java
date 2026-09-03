package com.example.devhire.repo;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.devhire.model.RecruiterProfile;

public interface RecruiterProfileRepository extends JpaRepository<RecruiterProfile, Long> {
    boolean existsByUserId(Long userId);
    
    Optional<RecruiterProfile> findByUserEmail(String email);
}