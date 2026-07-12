package com.scholarly.auth.repository;

import com.scholarly.auth.model.StudentProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface StudentProfileRepository extends JpaRepository<StudentProfile, UUID> {
    boolean existsByEnrollmentNumber(String enrollmentNumber);
}
