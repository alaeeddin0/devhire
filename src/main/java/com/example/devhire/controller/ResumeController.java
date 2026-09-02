package com.example.devhire.controller;

import com.example.devhire.dto.resume.ResumeFileDownload;
import com.example.devhire.dto.resume.ResumeResponse;
import com.example.devhire.service.ResumeService;
import lombok.RequiredArgsConstructor;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/candidate-profiles")
@RequiredArgsConstructor
public class ResumeController {

        private final ResumeService resumeService;

        @GetMapping("/{candidateProfileId}/resumes")
        public ResponseEntity<List<ResumeResponse>> getResumesByCandidate(
                        @PathVariable Long candidateProfileId) {
                return ResponseEntity.ok(
                                resumeService.getResumesByCandidate(candidateProfileId));
        }

        @GetMapping("/{candidateProfileId}/resumes/{resumeId}")
        public ResponseEntity<ResumeResponse> getResumeById(
                        @PathVariable Long candidateProfileId,
                        @PathVariable Long resumeId) {
                return ResponseEntity.ok(
                                resumeService.getResumeById(
                                                candidateProfileId,
                                                resumeId));
        }

        @PostMapping(value = "/{candidateProfileId}/resume", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
        public ResponseEntity<ResumeResponse> uploadResume(
                        @PathVariable Long candidateProfileId,
                        @RequestParam MultipartFile file) {
                return ResponseEntity.ok(
                                resumeService.uploadResume(candidateProfileId, file));
        }

        @GetMapping("/{candidateProfileId}/resumes/{resumeId}/download")
        public ResponseEntity<Resource> downloadResume(
                        @PathVariable Long candidateProfileId,
                        @PathVariable Long resumeId) {
                ResumeFileDownload file = resumeService.downloadResume(
                                candidateProfileId,
                                resumeId);

                return ResponseEntity.ok()
                                .contentType(MediaType.APPLICATION_PDF)
                                .header(
                                                HttpHeaders.CONTENT_DISPOSITION,
                                                ContentDisposition.attachment()
                                                                .filename(
                                                                                file.originalFileName(),
                                                                                StandardCharsets.UTF_8)
                                                                .build()
                                                                .toString())
                                .body(file.resource());
        }

        @DeleteMapping("/{candidateProfileId}/resumes/{resumeId}")
        public ResponseEntity<Void> deleteResume(
                        @PathVariable Long candidateProfileId,
                        @PathVariable Long resumeId) {
                resumeService.deleteResume(candidateProfileId, resumeId);

                return ResponseEntity.noContent().build();
        }
}
