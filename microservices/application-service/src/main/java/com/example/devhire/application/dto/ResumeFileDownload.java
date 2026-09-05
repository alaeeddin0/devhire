package com.example.devhire.application.dto;

public record ResumeFileDownload(
        String originalFileName,
        String contentType,
        byte[] content) {
}