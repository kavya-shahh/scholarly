package com.scholarly.application.service;

import com.scholarly.application.model.Application;
import com.scholarly.application.model.ApplicationStatus;
import com.scholarly.application.repository.ApplicationRepository;
import com.scholarly.auth.model.User;
import com.scholarly.auth.repository.UserRepository;
import com.scholarly.common.service.FileStorageService;
import com.scholarly.scholarship.model.Scholarship;
import com.scholarly.scholarship.repository.ScholarshipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ApplicationService {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ScholarshipRepository scholarshipRepository;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private TranscriptParserService transcriptParserService;

    @Transactional
    public Application apply(String studentEmail, UUID scholarshipId, BigDecimal gpaEntered, MultipartFile file) {
        // 1. Retrieve the student profile
        User student = userRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));

        if (student.getStudentProfile() == null) {
            throw new IllegalArgumentException("User does not have a student profile configured");
        }

        // 2. Retrieve the scholarship program
        Scholarship scholarship = scholarshipRepository.findById(scholarshipId)
                .orElseThrow(() -> new IllegalArgumentException("Scholarship program not found"));

        // 3. Prevent duplicate submissions
        if (applicationRepository.existsByStudentIdAndScholarshipId(student.getId(), scholarshipId)) {
            throw new IllegalArgumentException("You have already submitted an application for this scholarship");
        }

        // 4. Validate GPA eligibility criteria
        BigDecimal profileGpa = student.getStudentProfile().getGpa();
        if (profileGpa.compareTo(scholarship.getMinGpa()) < 0) {
            throw new IllegalArgumentException("Your GPA (" + profileGpa + ") does not meet the minimum requirement (" + scholarship.getMinGpa() + ") for this scholarship");
        }

        // 5. Check if scholarship deadline has passed
        if (LocalDateTime.now().isAfter(scholarship.getDeadline())) {
            throw new IllegalArgumentException("The application deadline for this scholarship has passed");
        }

        // 6. Write binary to disk
        String fileUrl = fileStorageService.storeFile(file);

        // 7. Parse transcript using PDFBox OCR
        BigDecimal gpaExtracted = transcriptParserService.extractGpa(fileUrl);
        
        ApplicationStatus status = ApplicationStatus.SUBMITTED;
        String systemComments = null;

        if (gpaExtracted == null) {
            // Parsing failure (scanned PDF or no matches)
            status = ApplicationStatus.PENDING_VERIFICATION;
            systemComments = "System Flag: OCR could not parse GPA. Scanned document or unrecognized format.";
        } else {
            // Compare extracted GPA with self-declared GPA (margin tolerance: 0.02)
            BigDecimal difference = gpaExtracted.subtract(gpaEntered).abs();
            if (difference.compareTo(new BigDecimal("0.02")) > 0) {
                status = ApplicationStatus.PENDING_VERIFICATION;
                systemComments = String.format("System Flag: Mismatch detected. Form submitted CGPA: %s, but OCR extracted CGPA from transcript: %s.", gpaEntered, gpaExtracted);
            }
        }

        // 8. Instantiate and save application record
        Application application = new Application(
                student,
                scholarship,
                status,
                gpaEntered,
                fileUrl
        );
        application.setGpaExtracted(gpaExtracted);
        application.setFacultyComments(systemComments);

        return applicationRepository.save(application);
    }

    @Transactional(readOnly = true)
    public List<Application> getStudentApplications(String studentEmail) {
        return applicationRepository.findByStudentEmailOrderByAppliedAtDesc(studentEmail);
    }
}
