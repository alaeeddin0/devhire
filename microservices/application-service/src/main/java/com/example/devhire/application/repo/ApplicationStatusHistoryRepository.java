package com.example.devhire.application.repo;

import com.example.devhire.application.model.ApplicationStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApplicationStatusHistoryRepository
        extends JpaRepository<ApplicationStatusHistory, Long> {

    List<ApplicationStatusHistory> findAllByJobApplicationIdOrderByChangedAtDesc(
            Long jobApplicationId);
}