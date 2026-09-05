package com.example.devhire.application.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "job_applications", uniqueConstraints = @UniqueConstraint(name = "uk_candidate_job_offer", columnNames = {
        "candidate_user_id", "job_offer_id" }))
@Getter
@Setter
@NoArgsConstructor
public class JobApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*
     * Références externes :
     * - candidateUserId → auth-service
     * - jobOfferId → job-service
     * - resumeId → stockage CV de ce service, ajouté ensuite
     */
    @Column(name = "candidate_user_id", nullable = false)
    private Long candidateUserId;

    @Column(name = "job_offer_id", nullable = false)
    private Long jobOfferId;

    @Column(name = "resume_id", nullable = false)
    private Long resumeId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ApplicationStatus status;

    @Column(columnDefinition = "TEXT")
    private String coverLetter;

    @Column(nullable = false, updatable = false)
    private LocalDateTime appliedAt;

    @PrePersist
    void onCreate() {
        if (status == null) {
            status = ApplicationStatus.PENDING;
        }

        if (appliedAt == null) {
            appliedAt = LocalDateTime.now();
        }
    }
}