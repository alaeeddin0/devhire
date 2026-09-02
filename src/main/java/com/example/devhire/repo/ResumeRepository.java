package com.example.devhire.repo;

import com.example.devhire.model.Resume;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;


public interface ResumeRepository extends JpaRepository<Resume, Long> {

    List<Resume> findAllByCandidateIdOrderByUploadedAtDesc(
        Long candidateProfileId
);}
