package com.example.devhire.job.repo;

import com.example.devhire.job.model.JobOffer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface JobOfferRepository
        extends JpaRepository<JobOffer, Long> {

    @Query("""
            SELECT offer
            FROM JobOffer offer
            WHERE (
                COALESCE(:keyword, '') = ''
                OR LOWER(offer.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(offer.company) LIKE LOWER(CONCAT('%', :keyword, '%'))
            )
            AND (
                COALESCE(:location, '') = ''
                OR LOWER(offer.location) = LOWER(:location)
            )
            AND (
                COALESCE(:workMode, '') = ''
                OR offer.workMode = :workMode
            )
            AND (
                COALESCE(:offerType, '') = ''
                OR offer.offerType = :offerType
            )
            """)
    Page<JobOffer> search(
            @Param("keyword") String keyword,
            @Param("location") String location,
            @Param("workMode") String workMode,
            @Param("offerType") String offerType,
            Pageable pageable);
}