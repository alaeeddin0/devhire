package com.example.devhire.job.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "saved_job_offers", uniqueConstraints = @UniqueConstraint(name = "uk_saved_offer_candidate_offer", columnNames = {
        "candidate_user_id", "job_offer_id" }))
@Getter
@Setter
@NoArgsConstructor
public class SavedJobOffer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "candidate_user_id", nullable = false)
    private Long candidateUserId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "job_offer_id", nullable = false)
    private JobOffer jobOffer;

    @Column(nullable = false, updatable = false)
    private LocalDateTime savedAt;

    @PrePersist
    void onCreate() {
        if (savedAt == null) {
            savedAt = LocalDateTime.now();
        }
    }
}