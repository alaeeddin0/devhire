package com.example.devhire.repo.resume;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.devhire.model.resume.Resume;

import java.util.List;


public interface ResumeRepository extends JpaRepository<Resume, Long> {

    List<Resume> findAllByCandidateIdOrderByUploadedAtDesc(
        Long candidateProfileId
);}
