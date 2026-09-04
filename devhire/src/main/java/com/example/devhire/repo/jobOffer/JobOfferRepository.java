package com.example.devhire.repo.jobOffer;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.devhire.model.jobOffer.JobOffer;

public interface JobOfferRepository
        extends JpaRepository<JobOffer, Long> {

    @Query("""
            SELECT jobOffer
            FROM JobOffer jobOffer
            WHERE (
                :keyword IS NULL
                OR LOWER(jobOffer.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(jobOffer.company) LIKE LOWER(CONCAT('%', :keyword, '%'))
            )
            AND (
                :location IS NULL
                OR LOWER(jobOffer.location) = LOWER(:location)
            )
            AND (
                :workMode IS NULL
                OR jobOffer.workMode = :workMode
            )
            AND (
                :offerType IS NULL
                OR jobOffer.offerType = :offerType
            )
            """)
    Page<JobOffer> search(
            @Param("keyword") String keyword,
            @Param("location") String location,
            @Param("workMode") String workMode,
            @Param("offerType") String offerType,
            Pageable pageable);
}
