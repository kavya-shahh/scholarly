package com.scholarly.application.model;

import com.scholarly.auth.model.User;
import com.scholarly.scholarship.model.Scholarship;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "applications")
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scholarship_id", nullable = false)
    private Scholarship scholarship;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationStatus status;

    @Column(name = "gpa_entered", nullable = false, precision = 4, scale = 2)
    private BigDecimal gpaEntered;

    @Column(name = "gpa_extracted", precision = 4, scale = 2)
    private BigDecimal gpaExtracted;

    @Column(name = "transcript_url", nullable = false)
    private String transcriptUrl;

    @Column(name = "faculty_comments", length = 1000)
    private String facultyComments;

    @CreationTimestamp
    @Column(name = "applied_at", nullable = false, updatable = false)
    private LocalDateTime appliedAt;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "verified_by_id")
    private User verifiedBy;

    // Constructors
    public Application() {}

    public Application(User student, Scholarship scholarship, ApplicationStatus status, BigDecimal gpaEntered, String transcriptUrl) {
        this.student = student;
        this.scholarship = scholarship;
        this.status = status;
        this.gpaEntered = gpaEntered;
        this.transcriptUrl = transcriptUrl;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public User getStudent() {
        return student;
    }

    public void setStudent(User student) {
        this.student = student;
    }

    public Scholarship getScholarship() {
        return scholarship;
    }

    public void setScholarship(Scholarship scholarship) {
        this.scholarship = scholarship;
    }

    public ApplicationStatus getStatus() {
        return status;
    }

    public void setStatus(ApplicationStatus status) {
        this.status = status;
    }

    public BigDecimal getGpaEntered() {
        return gpaEntered;
    }

    public void setGpaEntered(BigDecimal gpaEntered) {
        this.gpaEntered = gpaEntered;
    }

    public BigDecimal getGpaExtracted() {
        return gpaExtracted;
    }

    public void setGpaExtracted(BigDecimal gpaExtracted) {
        this.gpaExtracted = gpaExtracted;
    }

    public String getTranscriptUrl() {
        return transcriptUrl;
    }

    public void setTranscriptUrl(String transcriptUrl) {
        this.transcriptUrl = transcriptUrl;
    }

    public String getFacultyComments() {
        return facultyComments;
    }

    public void setFacultyComments(String facultyComments) {
        this.facultyComments = facultyComments;
    }

    public LocalDateTime getAppliedAt() {
        return appliedAt;
    }

    public void setAppliedAt(LocalDateTime appliedAt) {
        this.appliedAt = appliedAt;
    }

    public LocalDateTime getVerifiedAt() {
        return verifiedAt;
    }

    public void setVerifiedAt(LocalDateTime verifiedAt) {
        this.verifiedAt = verifiedAt;
    }

    public User getVerifiedBy() {
        return verifiedBy;
    }

    public void setVerifiedBy(User verifiedBy) {
        this.verifiedBy = verifiedBy;
    }
}
