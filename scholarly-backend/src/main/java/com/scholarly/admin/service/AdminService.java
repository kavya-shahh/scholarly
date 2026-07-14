package com.scholarly.admin.service;

import com.scholarly.admin.dto.AdminStatsResponse;
import com.scholarly.application.repository.ApplicationRepository;
import com.scholarly.auth.model.Role;
import com.scholarly.auth.repository.UserRepository;
import com.scholarly.scholarship.repository.ScholarshipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Transactional(readOnly = true)
public class AdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ScholarshipRepository scholarshipRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    public AdminStatsResponse getSystemStats() {
        long totalStudents = userRepository.countByRole(Role.STUDENT);
        long totalScholarships = scholarshipRepository.count();
        long totalApplications = applicationRepository.count();
        BigDecimal totalAllocatedFunds = applicationRepository.sumAllocatedFunds();

        return new AdminStatsResponse(
                totalStudents,
                totalScholarships,
                totalApplications,
                totalAllocatedFunds
        );
    }
}
