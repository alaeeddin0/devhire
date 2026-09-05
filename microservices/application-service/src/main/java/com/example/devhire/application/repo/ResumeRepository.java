package com.example.devhire.application.repo;

import com.example.devhire.application.model.Resume;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResumeRepository extends JpaRepository<Resume, Long> {

    List<Resume> findAllByCandidateUserIdOrderByUploadedAtDesc(
            Long candidateUserId);
}