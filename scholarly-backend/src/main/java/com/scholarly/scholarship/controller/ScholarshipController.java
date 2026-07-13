package com.scholarly.scholarship.controller;

import com.scholarly.scholarship.model.Scholarship;
import com.scholarly.scholarship.service.ScholarshipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/scholarships")
public class ScholarshipController {

    @Autowired
    private ScholarshipService scholarshipService;

    @GetMapping
    public ResponseEntity<List<Scholarship>> getActiveScholarships() {
        List<Scholarship> scholarships = scholarshipService.getActiveScholarships();
        return ResponseEntity.ok(scholarships);
    }

    @PostMapping
    // Restrict scholarship creation strictly to ADMIN role
    public ResponseEntity<Scholarship> createScholarship(@RequestBody Scholarship scholarship) {
        Scholarship created = scholarshipService.createScholarship(scholarship);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}
