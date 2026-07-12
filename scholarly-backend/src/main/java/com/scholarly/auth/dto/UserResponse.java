package com.scholarly.auth.dto;

import com.scholarly.auth.model.Role;

import java.math.BigDecimal;
import java.util.UUID;

public record UserResponse(
    UUID id,
    String email,
    String firstName,
    String lastName,
    Role role,
    
    // Student-specific fields (will be null for FACULTY/ADMIN)
    BigDecimal gpa,
    String department,
    String enrollmentNumber
) {}
