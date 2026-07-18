package com.scholarly.admin.service;

import com.scholarly.admin.dto.AdminStatsResponse;
import com.scholarly.application.model.ApplicationStatus;
import com.scholarly.application.repository.ApplicationRepository;
import com.scholarly.auth.model.Role;
import com.scholarly.auth.repository.UserRepository;
import com.scholarly.scholarship.repository.ScholarshipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        long pendingApprovals = applicationRepository.countByStatus(ApplicationStatus.PENDING_VERIFICATION);
        long approvedScholarships = applicationRepository.countByStatus(ApplicationStatus.APPROVED)
                + applicationRepository.countByStatus(ApplicationStatus.SUBMITTED);

        return new AdminStatsResponse(
                totalStudents,
                totalScholarships,
                totalApplications,
                pendingApprovals,
                approvedScholarships
        );
    }
}
