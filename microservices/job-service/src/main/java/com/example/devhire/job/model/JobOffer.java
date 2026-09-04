package com.example.devhire.job.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "job_offers")
@Getter
@Setter
@NoArgsConstructor
public class JobOffer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, length = 150)
    private String company;

    @Column(nullable = false, length = 100)
    private String location;

    @Column(nullable = false, length = 30)
    private String workMode;

    @Column(nullable = false, length = 30)
    private String offerType;

    @Column(precision = 12, scale = 2)
    private BigDecimal salaryMin;

    @Column(precision = 12, scale = 2)
    private BigDecimal salaryMax;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /*
     * Identifiant du recruteur dans auth-service.
     * Pas de @ManyToOne : les services ne partagent pas leurs entités JPA.
     */
    @Column(nullable = false)
    private Long recruiterUserId;

    @PrePersist
    void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}