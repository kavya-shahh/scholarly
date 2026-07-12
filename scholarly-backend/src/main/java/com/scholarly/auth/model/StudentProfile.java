package com.scholarly.auth.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "student_profiles")
public class StudentProfile {

    @Id
    @Column(name = "user_id")
    private UUID userId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false, precision = 4, scale = 2)
    private BigDecimal gpa;

    @Column(nullable = false)
    private String department;

    @Column(name = "enrollment_number", unique = true, nullable = false)
    private String enrollmentNumber;

    // Constructors
    public StudentProfile() {}

    public StudentProfile(User user, BigDecimal gpa, String department, String enrollmentNumber) {
        this.user = user;
        this.gpa = gpa;
        this.department = department;
        this.enrollmentNumber = enrollmentNumber;
    }

    // Getters and Setters
    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public BigDecimal getGpa() {
        return gpa;
    }

    public void setGpa(BigDecimal gpa) {
        this.gpa = gpa;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getEnrollmentNumber() {
        return enrollmentNumber;
    }

    public void setEnrollmentNumber(String enrollmentNumber) {
        this.enrollmentNumber = enrollmentNumber;
    }
}
