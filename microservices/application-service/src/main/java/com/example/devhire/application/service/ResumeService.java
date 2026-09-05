package com.example.devhire.application.service;

import com.example.devhire.application.dto.ResumeFileDownload;
import com.example.devhire.application.dto.ResumeResponse;
import com.example.devhire.application.exception.ResourceNotFoundException;
import com.example.devhire.application.model.Resume;
import com.example.devhire.application.repo.ResumeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.example.devhire.application.repo.JobApplicationRepository;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ResumeService {

        private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;
        private static final String PDF_CONTENT_TYPE = "application/pdf";

        private final ResumeRepository resumeRepository;
        private final JobApplicationRepository jobApplicationRepository;

        @Value("${app.storage.resume-directory}")
        private String resumeDirectory;

        public ResumeResponse upload(
                        Long candidateUserId,
                        MultipartFile file) {

                validatePdf(file);

                String originalFileName = sanitizeFileName(
                                file.getOriginalFilename());

                String storedFileName = UUID.randomUUID() + ".pdf";

                try {
                        Path directory = Path.of(resumeDirectory)
                                        .toAbsolutePath()
                                        .normalize();

                        Files.createDirectories(directory);

                        Path destination = directory.resolve(storedFileName)
                                        .normalize();

                        if (!destination.startsWith(directory)) {
                                throw new IllegalArgumentException(
                                                "Nom de fichier invalide.");
                        }

                        Files.copy(
                                        file.getInputStream(),
                                        destination,
                                        StandardCopyOption.REPLACE_EXISTING);

                } catch (IOException exception) {
                        throw new IllegalStateException(
                                        "Impossible d'enregistrer le CV.", exception);
                }

                Resume resume = new Resume();
                resume.setCandidateUserId(candidateUserId);
                resume.setOriginalFileName(originalFileName);
                resume.setStoredFileName(storedFileName);
                resume.setContentType(PDF_CONTENT_TYPE);
                resume.setFileSize(file.getSize());

                return toResponse(resumeRepository.save(resume));
        }

        @Transactional(readOnly = true)
        public List<ResumeResponse> getMyResumes(Long candidateUserId) {
                return resumeRepository
                                .findAllByCandidateUserIdOrderByUploadedAtDesc(
                                                candidateUserId)
                                .stream()
                                .map(this::toResponse)
                                .toList();
        }

        @Transactional(readOnly = true)
        public ResumeFileDownload download(
                        Long candidateUserId,
                        Long resumeId) {

                Resume resume = getOwnedResume(candidateUserId, resumeId);

                try {
                        Path filePath = resolveStoredFile(resume.getStoredFileName());

                        return new ResumeFileDownload(
                                        resume.getOriginalFileName(),
                                        resume.getContentType(),
                                        Files.readAllBytes(filePath));

                } catch (IOException exception) {
                        throw new IllegalStateException(
                                        "Impossible de lire le CV.", exception);
                }
        }

        public void delete(Long candidateUserId, Long resumeId) {
                Resume resume = getOwnedResume(candidateUserId, resumeId);

                if (jobApplicationRepository.existsByResumeId(resumeId)) {
                        throw new IllegalArgumentException(
                                        "Ce CV est utilisé par une candidature et ne peut pas être supprimé.");
                }

                try {
                        Files.deleteIfExists(
                                        resolveStoredFile(resume.getStoredFileName()));

                } catch (IOException exception) {
                        throw new IllegalStateException(
                                        "Impossible de supprimer le fichier CV.", exception);
                }

                resumeRepository.delete(resume);
        }

        private Resume getOwnedResume(
                        Long candidateUserId,
                        Long resumeId) {

                Resume resume = resumeRepository.findById(resumeId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "CV introuvable."));

                if (!resume.getCandidateUserId().equals(candidateUserId)) {
                        throw new AccessDeniedException(
                                        "Vous ne pouvez pas accéder à ce CV.");
                }

                return resume;
        }

        private void validatePdf(MultipartFile file) {
                if (file == null || file.isEmpty()) {
                        throw new IllegalArgumentException(
                                        "Le fichier CV est obligatoire.");
                }

                if (file.getSize() > MAX_FILE_SIZE) {
                        throw new IllegalArgumentException(
                                        "Le CV ne doit pas dépasser 5 Mo.");
                }

                if (!PDF_CONTENT_TYPE.equalsIgnoreCase(
                                file.getContentType())) {
                        throw new IllegalArgumentException(
                                        "Seuls les fichiers PDF sont acceptés.");
                }
        }

        private String sanitizeFileName(String fileName) {
                if (fileName == null || fileName.isBlank()) {
                        throw new IllegalArgumentException(
                                        "Nom de fichier invalide.");
                }

                String sanitized = Path.of(fileName)
                                .getFileName()
                                .toString();

                if (!sanitized.toLowerCase().endsWith(".pdf")) {
                        throw new IllegalArgumentException(
                                        "Le CV doit avoir l'extension .pdf.");
                }

                return sanitized;
        }

        private Path resolveStoredFile(String storedFileName) {
                Path directory = Path.of(resumeDirectory)
                                .toAbsolutePath()
                                .normalize();

                Path filePath = directory.resolve(storedFileName)
                                .normalize();

                if (!filePath.startsWith(directory)) {
                        throw new IllegalArgumentException(
                                        "Chemin de fichier invalide.");
                }

                return filePath;
        }

        private ResumeResponse toResponse(Resume resume) {
                return new ResumeResponse(
                                resume.getId(),
                                resume.getOriginalFileName(),
                                resume.getContentType(),
                                resume.getFileSize(),
                                resume.getUploadedAt());
        }
}