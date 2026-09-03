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
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/resumes")
@RequiredArgsConstructor
public class ResumeController {

        private final ResumeService resumeService;

        @GetMapping
        public ResponseEntity<List<ResumeResponse>> getMyResumes(
                        Authentication authentication) {
                return ResponseEntity.ok(
                                resumeService.getMyResumes(authentication.getName()));
        }

        @GetMapping("/{resumeId}")
        public ResponseEntity<ResumeResponse> getResumeById(
                        @PathVariable Long resumeId,
                        Authentication authentication) {
                return ResponseEntity.ok(
                                resumeService.getResumeById(
                                                authentication.getName(),
                                                resumeId));
        }

        @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
        public ResponseEntity<ResumeResponse> uploadResume(
                        Authentication authentication,
                        @RequestParam MultipartFile file) {
                return ResponseEntity.ok(
                                resumeService.uploadResume(
                                                authentication.getName(),
                                                file));
        }

        @GetMapping("/{resumeId}/download")
        public ResponseEntity<Resource> downloadResume(
                        @PathVariable Long resumeId,
                        Authentication authentication) {
                ResumeFileDownload file = resumeService.downloadResume(
                                authentication.getName(),
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

        @DeleteMapping("/{resumeId}")
        public ResponseEntity<Void> deleteResume(
                        @PathVariable Long resumeId,
                        Authentication authentication) {
                resumeService.deleteResume(
                                authentication.getName(),
                                resumeId);

                return ResponseEntity.noContent().build();
        }
}
