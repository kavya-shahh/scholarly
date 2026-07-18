package com.scholarly.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import jakarta.validation.constraints.Size;

public record VerifyApplicationRequest(
    @NotBlank(message = "Verification status is required")
    @Pattern(regexp = "APPROVED|REJECTED", message = "Status must be either APPROVED or REJECTED")
    String status,
    
    @Size(max = 1000, message = "Remarks cannot exceed 1000 characters")
    String remarks
) {}
