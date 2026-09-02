package com.example.devhire.service;

import com.example.devhire.dto.resume.ResumeFileDownload;
import com.example.devhire.dto.resume.ResumeResponse;
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
import java.util.List;
import java.util.UUID;
import com.example.devhire.repo.JobApplicationRepository;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import java.net.MalformedURLException;

@Service
public class ResumeService {

        private final ResumeRepository resumeRepository;
        private final CandidateProfileRepository candidateProfileRepository;
        private final Path resumeDirectory;
        private final JobApplicationRepository jobApplicationRepository;

        public ResumeService(
                        ResumeRepository resumeRepository,
                        CandidateProfileRepository candidateProfileRepository,
                        @Value("${app.storage.resume-directory}") String resumeDirectory,
                        JobApplicationRepository jobApplicationRepository) {
                this.resumeRepository = resumeRepository;
                this.candidateProfileRepository = candidateProfileRepository;
                this.resumeDirectory = Path.of(resumeDirectory)
                                .toAbsolutePath()
                                .normalize();
                this.jobApplicationRepository = jobApplicationRepository;
        }

        public List<ResumeResponse> getResumesByCandidate(
                        Long candidateProfileId) {
                if (!candidateProfileRepository.existsById(candidateProfileId)) {
                        throw new ResourceNotFoundException(
                                        "Profil candidat introuvable avec l'id : "
                                                        + candidateProfileId);
                }

                return resumeRepository
                                .findAllByCandidateIdOrderByUploadedAtDesc(
                                                candidateProfileId)
                                .stream()
                                .map(this::toResponse)
                                .toList();
        }

        public ResumeResponse getResumeById(
                        Long candidateProfileId,
                        Long resumeId) {
                Resume resume = getResumeEntityById(resumeId);

                if (!resume.getCandidate().getId().equals(candidateProfileId)) {
                        throw new IllegalArgumentException(
                                        "Ce CV n'appartient pas à ce candidat.");
                }

                return toResponse(resume);
        }

        private Resume getResumeEntityById(Long id) {
                return resumeRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "CV introuvable avec l'id : " + id));
        }

        @Transactional
        public ResumeResponse uploadResume(
                        Long candidateProfileId,
                        MultipartFile file) {
                validatePdf(file);

                CandidateProfile candidate = candidateProfileRepository
                                .findById(candidateProfileId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Profil candidat introuvable avec l'id : "
                                                                + candidateProfileId));

                try {
                        Files.createDirectories(resumeDirectory);

                        String originalFileName = file.getOriginalFilename();
                        String storedFileName = UUID.randomUUID() + ".pdf";
                        Path targetPath = resumeDirectory.resolve(storedFileName);

                        Files.copy(
                                        file.getInputStream(),
                                        targetPath,
                                        StandardCopyOption.REPLACE_EXISTING);

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
                                        exception);
                }
        }

        private void validatePdf(MultipartFile file) {
                if (file.isEmpty()) {
                        throw new IllegalArgumentException(
                                        "Le fichier CV est obligatoire.");
                }

                if (!"application/pdf".equals(file.getContentType())) {
                        throw new IllegalArgumentException(
                                        "Seuls les fichiers PDF sont acceptés.");
                }
        }

        private ResumeResponse toResponse(Resume resume) {
                return new ResumeResponse(
                                resume.getId(),
                                resume.getCandidate().getId(),
                                resume.getOriginalFileName(),
                                resume.getContentType(),
                                resume.getFileSize(),
                                resume.getUploadedAt());
        }

        public ResumeFileDownload downloadResume(
                        Long candidateProfileId,
                        Long resumeId) {
                Resume resume = getResumeEntityById(resumeId);

                if (!resume.getCandidate().getId().equals(candidateProfileId)) {
                        throw new IllegalArgumentException(
                                        "Ce CV n'appartient pas à ce candidat.");
                }

                Path filePath = resumeDirectory
                                .resolve(resume.getStoredFileName())
                                .normalize();

                if (!filePath.startsWith(resumeDirectory)) {
                        throw new IllegalArgumentException(
                                        "Chemin de fichier CV invalide.");
                }

                try {
                        Resource resource = new UrlResource(filePath.toUri());

                        if (!resource.exists() || !resource.isReadable()) {
                                throw new ResourceNotFoundException(
                                                "Le fichier du CV est introuvable.");
                        }

                        return new ResumeFileDownload(
                                        resource,
                                        resume.getOriginalFileName());

                } catch (MalformedURLException exception) {
                        throw new IllegalStateException(
                                        "Impossible de lire le fichier CV.",
                                        exception);
                }
        }
        
        @Transactional
        public void deleteResume(
                        Long candidateProfileId,
                        Long resumeId) {
                Resume resume = getResumeEntityById(resumeId);

                if (!resume.getCandidate().getId().equals(candidateProfileId)) {
                        throw new IllegalArgumentException(
                                        "Ce CV n'appartient pas à ce candidat.");
                }

                if (jobApplicationRepository.existsByResumeId(resumeId)) {
                        throw new IllegalArgumentException(
                                        "Ce CV ne peut pas être supprimé car il est lié à une candidature.");
                }

                Path filePath = resumeDirectory
                                .resolve(resume.getStoredFileName())
                                .normalize();

                if (!filePath.startsWith(resumeDirectory)) {
                        throw new IllegalArgumentException(
                                        "Chemin de fichier CV invalide.");
                }

                resumeRepository.delete(resume);

                try {
                        Files.deleteIfExists(filePath);
                } catch (IOException exception) {
                        throw new IllegalStateException(
                                        "Impossible de supprimer le fichier CV.",
                                        exception);
                }
        }
}
