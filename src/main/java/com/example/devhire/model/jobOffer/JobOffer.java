package com.example.devhire.model.jobOffer;

import com.example.devhire.model.recruiter.RecruiterProfile;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "job_offers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobOffer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, length = 100)
    private String company;

    @Column(nullable = false, length = 100)
    private String location;

    @Column(nullable = false, length = 30)
    private String workMode; // REMOTE, ONSITE, HYBRID

    @Column(nullable = false, length = 30)
    private String offerType;// PFE, INTERNSHIP, JOB

    @Column(precision = 12, scale = 2)
    private BigDecimal salaryMin;

    @Column(precision = 12, scale = 2)
    private BigDecimal salaryMax;

    private LocalDateTime createdAt;

    @ManyToOne(optional = false)
    @JoinColumn(name = "recruiter_profile_id", nullable = false)
    private RecruiterProfile recruiter;

    @PrePersist
    void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
