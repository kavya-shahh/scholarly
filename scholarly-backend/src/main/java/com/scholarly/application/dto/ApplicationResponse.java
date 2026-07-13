package com.scholarly.application.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record ApplicationResponse(
    UUID id,
    UUID scholarshipId,
    String scholarshipTitle,
    BigDecimal scholarshipAmount,
    String status,
    BigDecimal gpaEntered,
    BigDecimal gpaExtracted,
    String transcriptUrl,
    String facultyComments,
    LocalDateTime appliedAt
) {}
