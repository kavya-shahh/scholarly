package com.scholarly.application.repository;

import com.scholarly.application.model.Application;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, UUID> {
    
    // Fetch applications for a student, using EntityGraph to eagerly load the Scholarship details
    // to prevent N+1 query problems when rendering dashboards.
    @EntityGraph(attributePaths = {"scholarship"})
    List<Application> findByStudentEmailOrderByAppliedAtDesc(String email);

    boolean existsByStudentIdAndScholarshipId(UUID studentId, UUID scholarshipId);
}
