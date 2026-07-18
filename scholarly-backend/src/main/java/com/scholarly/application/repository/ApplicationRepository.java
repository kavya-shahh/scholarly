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

    boolean existsByStudentId(UUID studentId);

    @EntityGraph(attributePaths = {"scholarship", "student", "student.studentProfile"})
    List<Application> findAllByOrderByAppliedAtDesc();

    @EntityGraph(attributePaths = {"scholarship", "student", "student.studentProfile"})
    List<Application> findByStatusOrderByAppliedAtDesc(com.scholarly.application.model.ApplicationStatus status);

    @org.springframework.data.jpa.repository.Query("SELECT COALESCE(SUM(s.amount), 0) FROM Application a JOIN a.scholarship s WHERE a.status = com.scholarly.application.model.ApplicationStatus.APPROVED")
    java.math.BigDecimal sumAllocatedFunds();

    long countByStatus(com.scholarly.application.model.ApplicationStatus status);
}
