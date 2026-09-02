package com.example.devhire.controller;

import com.example.devhire.dto.ResumeResponse;
import com.example.devhire.service.ResumeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/candidate-profiles")
@RequiredArgsConstructor
public class ResumeController {

    private final ResumeService resumeService;

    @PostMapping(
            value = "/{candidateProfileId}/resume",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<ResumeResponse> uploadResume(
            @PathVariable Long candidateProfileId,
            @RequestParam MultipartFile file
    ) {
        return ResponseEntity.ok(
                resumeService.uploadResume(candidateProfileId, file)
        );
    }
}
