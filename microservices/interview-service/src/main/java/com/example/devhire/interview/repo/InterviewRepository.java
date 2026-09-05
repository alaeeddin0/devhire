package com.example.devhire.interview.repo;

import com.example.devhire.interview.model.Interview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InterviewRepository
        extends JpaRepository<Interview, Long> {

    List<Interview> findAllByCandidateUserIdOrderByScheduledAtAsc(
            Long candidateUserId);

    List<Interview> findAllByRecruiterUserIdOrderByScheduledAtAsc(
            Long recruiterUserId);
}