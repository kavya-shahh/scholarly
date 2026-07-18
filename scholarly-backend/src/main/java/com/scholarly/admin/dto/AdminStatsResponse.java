package com.scholarly.admin.dto;

public record AdminStatsResponse(
    long totalStudents,
    long totalScholarships,
    long totalApplications,
    long pendingApprovals,
    long approvedScholarships
) {}
