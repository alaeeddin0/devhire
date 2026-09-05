package com.example.devhire.interview.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "interviews")
@Getter
@Setter
@NoArgsConstructor
public class Interview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*
     * Référence externe vers application-service.
     */
    @Column(name = "job_application_id", nullable = false)
    private Long jobApplicationId;

    /*
     * Recruteur propriétaire, obtenu depuis le JWT.
     */
    @Column(name = "recruiter_user_id", nullable = false)
    private Long recruiterUserId;

    /*
     * Copie de la référence candidat depuis application-service.
     * Elle permet d'afficher les entretiens du candidat sans
     * relation JPA distante.
     */
    @Column(name = "candidate_user_id", nullable = false)
    private Long candidateUserId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private InterviewType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private InterviewStatus status;

    @Column(nullable = false)
    private LocalDateTime scheduledAt;

    @Column(nullable = false)
    private Integer durationMinutes;

    @Column(length = 500)
    private String meetingLink;

    @Column(length = 255)
    private String location;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() {
        if (status == null) {
            status = InterviewStatus.SCHEDULED;
        }

        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}