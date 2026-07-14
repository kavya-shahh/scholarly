package com.scholarly.application.controller;

import com.scholarly.application.dto.ApplicationResponse;
import com.scholarly.application.model.Application;
import com.scholarly.application.service.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/applications")
public class ApplicationController {

    @Autowired
    private ApplicationService applicationService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApplicationResponse> apply(
            @RequestParam("scholarshipId") UUID scholarshipId,
            @RequestParam("gpaEntered") BigDecimal gpaEntered,
            @RequestParam("file") MultipartFile file) {

        // Extract student email from SecurityContext principal
        String studentEmail = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Application app = applicationService.apply(studentEmail, scholarshipId, gpaEntered, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapToResponse(app));
    }

    @GetMapping("/me")
    public ResponseEntity<List<ApplicationResponse>> getMyApplications() {
        String studentEmail = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Application> apps = applicationService.getStudentApplications(studentEmail);
        
        List<ApplicationResponse> responses = apps.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
                
        return ResponseEntity.ok(responses);
    }

    @GetMapping
    public ResponseEntity<List<com.scholarly.application.dto.FacultyApplicationResponse>> getAllApplications(
            @RequestParam(value = "status", required = false) String statusStr) {
        
        com.scholarly.application.model.ApplicationStatus status = null;
        if (statusStr != null && !statusStr.trim().isEmpty()) {
            try {
                status = com.scholarly.application.model.ApplicationStatus.valueOf(statusStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid status filter option: " + statusStr);
            }
        }

        List<Application> apps = applicationService.getAllApplications(status);
        List<com.scholarly.application.dto.FacultyApplicationResponse> responses = apps.stream()
                .map(this::mapToFacultyResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}/verify")
    public ResponseEntity<com.scholarly.application.dto.FacultyApplicationResponse> verifyApplication(
            @PathVariable("id") UUID id,
            @jakarta.validation.Valid @RequestBody com.scholarly.application.dto.VerifyApplicationRequest request) {

        String reviewerEmail = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        com.scholarly.application.model.ApplicationStatus targetStatus = com.scholarly.application.model.ApplicationStatus.valueOf(request.status().toUpperCase());

        Application app = applicationService.verifyApplication(id, reviewerEmail, targetStatus, request.remarks());
        return ResponseEntity.ok(mapToFacultyResponse(app));
    }

    private ApplicationResponse mapToResponse(Application app) {
        return new ApplicationResponse(
                app.getId(),
                app.getScholarship().getId(),
                app.getScholarship().getTitle(),
                app.getScholarship().getAmount(),
                app.getStatus().name(),
                app.getGpaEntered(),
                app.getGpaExtracted(),
                app.getTranscriptUrl(),
                app.getFacultyComments(),
                app.getAppliedAt()
        );
    }

    private com.scholarly.application.dto.FacultyApplicationResponse mapToFacultyResponse(Application app) {
        String studentName = app.getStudent().getFirstName() + " " + app.getStudent().getLastName();
        String studentEmail = app.getStudent().getEmail();
        String studentEnrollment = "";
        String studentDepartment = "";
        BigDecimal studentProfileGpa = BigDecimal.ZERO;

        if (app.getStudent().getStudentProfile() != null) {
            studentEnrollment = app.getStudent().getStudentProfile().getEnrollmentNumber();
            studentDepartment = app.getStudent().getStudentProfile().getDepartment();
            studentProfileGpa = app.getStudent().getStudentProfile().getGpa();
        }

        return new com.scholarly.application.dto.FacultyApplicationResponse(
                app.getId(),
                app.getScholarship().getId(),
                app.getScholarship().getTitle(),
                app.getScholarship().getAmount(),
                studentName,
                studentEmail,
                studentEnrollment,
                studentDepartment,
                studentProfileGpa,
                app.getStatus().name(),
                app.getGpaEntered(),
                app.getGpaExtracted(),
                app.getTranscriptUrl(),
                app.getFacultyComments(),
                app.getAppliedAt()
        );
    }
}
