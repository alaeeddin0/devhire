package com.example.devhire.service;

import com.example.devhire.dto.ResumeResponse;
import com.example.devhire.exception.ResourceNotFoundException;
import com.example.devhire.model.CandidateProfile;
import com.example.devhire.model.Resume;
import com.example.devhire.repo.CandidateProfileRepository;
import com.example.devhire.repo.ResumeRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class ResumeService {

    private final ResumeRepository resumeRepository;
    private final CandidateProfileRepository candidateProfileRepository;
    private final Path resumeDirectory;

        public ResumeService(
            ResumeRepository resumeRepository,
            CandidateProfileRepository candidateProfileRepository,
            @Value("${app.storage.resume-directory}") String resumeDirectory
    ) {
        this.resumeRepository = resumeRepository;
        this.candidateProfileRepository = candidateProfileRepository;
        this.resumeDirectory = Path.of(resumeDirectory)
                .toAbsolutePath()
                .normalize();
    }

        @Transactional
        public ResumeResponse uploadResume(
                Long candidateProfileId,
                MultipartFile file
        ) {
        validatePdf(file);

        CandidateProfile candidate = candidateProfileRepository
                .findById(candidateProfileId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Profil candidat introuvable avec l'id : "
                                + candidateProfileId
                ));

        try {
                Files.createDirectories(resumeDirectory);

                String originalFileName = file.getOriginalFilename();
                String storedFileName = UUID.randomUUID() + ".pdf";
                Path targetPath = resumeDirectory.resolve(storedFileName);

                Files.copy(
                        file.getInputStream(),
                        targetPath,
                        StandardCopyOption.REPLACE_EXISTING
                );

                Resume resume = new Resume();
                resume.setCandidate(candidate);
                resume.setOriginalFileName(originalFileName);
                resume.setStoredFileName(storedFileName);
                resume.setContentType(file.getContentType());
                resume.setFileSize(file.getSize());
                resume.setUploadedAt(LocalDateTime.now());

                Resume savedResume = resumeRepository.save(resume);

                return toResponse(savedResume);

        } catch (IOException exception) {
                throw new IllegalStateException(
                        "Impossible de stocker le CV.",
                        exception
                );
        }
        }

    private void validatePdf(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException(
                    "Le fichier CV est obligatoire."
            );
        }

        if (!"application/pdf".equals(file.getContentType())) {
            throw new IllegalArgumentException(
                    "Seuls les fichiers PDF sont acceptés."
            );
        }
    }

    private ResumeResponse toResponse(Resume resume) {
        return new ResumeResponse(
                resume.getId(),
                resume.getCandidate().getId(),
                resume.getOriginalFileName(),
                resume.getContentType(),
                resume.getFileSize(),
                resume.getUploadedAt()
        );
    }
}
