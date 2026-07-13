package com.scholarly.scholarship.repository;

import com.scholarly.scholarship.model.Scholarship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ScholarshipRepository extends JpaRepository<Scholarship, UUID> {
    // Return all scholarships whose deadlines have not yet passed, ordered by nearest deadline first
    List<Scholarship> findByDeadlineAfterOrderByDeadlineAsc(LocalDateTime deadline);
}
