package com.scholarly.application.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record FacultyApplicationResponse(
    UUID id,
    UUID scholarshipId,
    String scholarshipTitle,
    BigDecimal scholarshipAmount,
    String studentName,
    String studentEmail,
    String studentEnrollment,
    String studentDepartment,
    BigDecimal studentProfileGpa,
    String status,
    BigDecimal gpaEntered,
    BigDecimal gpaExtracted,
    String transcriptUrl,
    String facultyComments,
    LocalDateTime appliedAt
) {}
