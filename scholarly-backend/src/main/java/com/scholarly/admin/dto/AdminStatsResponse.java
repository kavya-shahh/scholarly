package com.scholarly.admin.dto;

import java.math.BigDecimal;

public record AdminStatsResponse(
    long totalStudents,
    long totalScholarships,
    long totalApplications,
    BigDecimal totalAllocatedFunds
) {}
