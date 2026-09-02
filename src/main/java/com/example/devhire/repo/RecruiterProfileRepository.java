package com.example.devhire.repo;


import org.springframework.data.jpa.repository.JpaRepository;
import com.example.devhire.model.RecruiterProfile;

public interface RecruiterProfileRepository extends JpaRepository<RecruiterProfile, Long> {
    boolean existsByUserId(Long userId);
}