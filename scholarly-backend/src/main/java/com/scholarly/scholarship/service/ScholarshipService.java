package com.scholarly.scholarship.service;

import com.scholarly.scholarship.model.Scholarship;
import com.scholarly.scholarship.repository.ScholarshipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ScholarshipService {

    @Autowired
    private ScholarshipRepository scholarshipRepository;

    @Transactional(readOnly = true)
    public List<Scholarship> getActiveScholarships() {
        // Fetch active scholarships whose deadlines are in the future
        return scholarshipRepository.findByDeadlineAfterOrderByDeadlineAsc(LocalDateTime.now());
    }

    @Transactional
    public Scholarship createScholarship(Scholarship scholarship) {
        return scholarshipRepository.save(scholarship);
    }

    @Transactional(readOnly = true)
    public Scholarship getScholarshipById(UUID id) {
        return scholarshipRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Scholarship not found with ID: " + id));
    }
}
