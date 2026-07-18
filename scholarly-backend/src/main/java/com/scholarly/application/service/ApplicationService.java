package com.scholarly.application.service;

import com.scholarly.application.model.Application;
import com.scholarly.application.model.ApplicationStatus;
import com.scholarly.application.repository.ApplicationRepository;
import com.scholarly.auth.model.User;
import com.scholarly.auth.repository.UserRepository;
import com.scholarly.common.service.FileStorageService;
import com.scholarly.scholarship.model.Scholarship;
import com.scholarly.scholarship.repository.ScholarshipRepository;
import com.scholarly.auth.model.Role;
import com.scholarly.notification.service.EmailService;
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

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ApplicationService.class);

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

    @Autowired
    private EmailService emailService;

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

        // 3. Prevent multiple submissions (limit to one scholarship application at a time)
        if (applicationRepository.existsByStudentId(student.getId())) {
            throw new IllegalArgumentException("A student is eligible for only one scholarship at a time.");
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

        Application savedApplication = applicationRepository.save(application);

        // 9. Send email notifications safely
        try {
            String studentName = student.getFirstName() + " " + student.getLastName();
            emailService.sendApplicationSubmitted(student.getEmail(), studentName, scholarship.getTitle());

            if (savedApplication.getStatus() == ApplicationStatus.SUBMITTED) {
                emailService.sendApplicationApproved(
                        student.getEmail(),
                        studentName,
                        scholarship.getTitle(),
                        "Auto-Approved: OCR gradesheet verification matched and eligible."
                );
            }
        } catch (Exception e) {
            log.error("Failed to send transactional email for application submission", e);
        }

        log.info("Application successfully submitted by student [email: {}] for scholarship [id: {}, title: {}]. Routed to status: {}", 
                student.getEmail(), scholarship.getId(), scholarship.getTitle(), savedApplication.getStatus());

        return savedApplication;
    }

    @Transactional(readOnly = true)
    public List<Application> getStudentApplications(String studentEmail) {
        return applicationRepository.findByStudentEmailOrderByAppliedAtDesc(studentEmail);
    }

    @Transactional(readOnly = true)
    public List<Application> getAllApplications(ApplicationStatus status) {
        if (status == null) {
            return applicationRepository.findAllByOrderByAppliedAtDesc();
        }
        return applicationRepository.findByStatusOrderByAppliedAtDesc(status);
    }

    @Transactional
    public Application verifyApplication(UUID id, String reviewerEmail, ApplicationStatus status, String comments) {
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Application not found"));

        User reviewer = userRepository.findByEmail(reviewerEmail)
                .orElseThrow(() -> new IllegalArgumentException("Reviewer profile not found"));

        if (status != ApplicationStatus.APPROVED && status != ApplicationStatus.REJECTED) {
            throw new IllegalArgumentException("Invalid verification status transition: " + status);
        }

        application.setStatus(status);
        application.setFacultyComments(comments);
        application.setVerifiedAt(LocalDateTime.now());
        application.setVerifiedBy(reviewer);

        Application savedApplication = applicationRepository.save(application);

        // Send email notifications safely
        try {
            User student = savedApplication.getStudent();
            String studentName = student.getFirstName() + " " + student.getLastName();
            String scholarshipTitle = savedApplication.getScholarship().getTitle();

            if (savedApplication.getStatus() == ApplicationStatus.APPROVED) {
                emailService.sendApplicationApproved(
                        student.getEmail(),
                        studentName,
                        scholarshipTitle,
                        savedApplication.getFacultyComments()
                );
            } else if (savedApplication.getStatus() == ApplicationStatus.REJECTED) {
                emailService.sendApplicationRejected(
                        student.getEmail(),
                        studentName,
                        scholarshipTitle,
                        savedApplication.getFacultyComments()
                );
            }
        } catch (Exception e) {
            log.error("Failed to send transactional email for verification update", e);
        }

        log.info("Application [id: {}] successfully audited by faculty reviewer [email: {}]. Target Status: {}", 
                savedApplication.getId(), reviewer.getEmail(), savedApplication.getStatus());

        return savedApplication;
    }
}
