package com.scholarly.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record VerifyApplicationRequest(
    @NotBlank(message = "Verification status is required")
    @Pattern(regexp = "APPROVED|REJECTED", message = "Status must be either APPROVED or REJECTED")
    String status,
    
    String remarks
) {}
