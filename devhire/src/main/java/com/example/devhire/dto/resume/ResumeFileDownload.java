package com.example.devhire.dto.resume;

import org.springframework.core.io.Resource;

public record ResumeFileDownload(
        Resource resource,
        String originalFileName) {
}