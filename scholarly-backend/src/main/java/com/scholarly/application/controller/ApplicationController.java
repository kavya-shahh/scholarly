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
}
