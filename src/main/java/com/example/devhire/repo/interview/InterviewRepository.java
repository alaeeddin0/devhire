package com.example.devhire.repo.interview;

import com.example.devhire.model.interview.Interview;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;

public interface InterviewRepository extends JpaRepository<Interview, Long> {

    List<Interview> findAllByJobApplicationCandidateUserEmailOrderByScheduledAtAsc(
            String email);

    List<Interview> findAllByJobApplicationJobOfferRecruiterUserEmailOrderByScheduledAtAsc(
            String email);
}
