package com.example.devhire.application.controller;

import com.example.devhire.application.dto.ResumeFileDownload;
import com.example.devhire.application.dto.ResumeResponse;
import com.example.devhire.application.security.AuthenticatedUser;
import com.example.devhire.application.service.ResumeService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/resumes")
@RequiredArgsConstructor
public class ResumeController {

    private final ResumeService resumeService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResumeResponse> upload(
            Authentication authentication,
            @RequestParam("file") MultipartFile file) {

        return ResponseEntity.status(201)
                .body(resumeService.upload(
                        currentUser(authentication).id(),
                        file));
    }

    @GetMapping
    public ResponseEntity<List<ResumeResponse>> getMyResumes(
            Authentication authentication) {

        return ResponseEntity.ok(
                resumeService.getMyResumes(
                        currentUser(authentication).id()));
    }

    @GetMapping("/{resumeId}/download")
    public ResponseEntity<ByteArrayResource> download(
            @PathVariable Long resumeId,
            Authentication authentication) {

        ResumeFileDownload download = resumeService.download(
                currentUser(authentication).id(),
                resumeId);

        ByteArrayResource resource = new ByteArrayResource(
                download.content());

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(
                        download.contentType()))
                .contentLength(download.content().length)
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment()
                                .filename(download.originalFileName())
                                .build()
                                .toString())
                .body(resource);
    }

    @DeleteMapping("/{resumeId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long resumeId,
            Authentication authentication) {

        resumeService.delete(
                currentUser(authentication).id(),
                resumeId);

        return ResponseEntity.noContent().build();
    }

    private AuthenticatedUser currentUser(
            Authentication authentication) {

        return (AuthenticatedUser) authentication.getPrincipal();
    }
}