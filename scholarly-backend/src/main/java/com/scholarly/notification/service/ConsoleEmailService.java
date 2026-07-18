package com.scholarly.notification.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ConsoleEmailService implements EmailService {

    private static final Logger log = LoggerFactory.getLogger(ConsoleEmailService.class);

    @Override
    public void sendApplicationSubmitted(String toEmail, String studentName, String scholarshipTitle) {
        String subject = "Scholarship Application Receipt: " + scholarshipTitle;
        String body = "Dear " + studentName + ",\n\n" +
                "Your application for the \"" + scholarshipTitle
                + "\" scholarship has been successfully submitted.\n" +
                "It's auto-processed and routed to our audit/ faculty panel if and only if manual verification is required in case of cgpa mismatch by OCR.\n\n"
                +
                "You can track your application's review status directly on your Scholarly Dashboard.\n\n" +
                "Best regards,\n" +
                "Scholarly";

        printEmailBox(toEmail, subject, body);
    }

    @Override
    public void sendVerificationRequired(String toFacultyEmail, String studentName, String scholarshipTitle,
            String flagReason) {
        // NO-OP
    }

    @Override
    public void sendApplicationApproved(String toEmail, String studentName, String scholarshipTitle, String comments) {
        String subject = "Scholarship Approved! Congratulations: " + scholarshipTitle;
        String body = "Dear " + studentName + ",\n\n" +
                "Congratulations! Your application for the \"" + scholarshipTitle
                + "\" scholarship has been reviewed and APPROVED by the Faculty review panel.\n\n" +
                "Faculty Auditor Remarks:\n" +
                "\"" + (comments != null && !comments.trim().isEmpty() ? comments : "No remarks provided.") + "\"\n\n" +
                "Funding disbursement operations have been initialized.\n\n" +
                "Best regards,\n" +
                "Scholarly";

        printEmailBox(toEmail, subject, body);
    }

    @Override
    public void sendApplicationRejected(String toEmail, String studentName, String scholarshipTitle, String comments) {
        String subject = "Scholarship Application Update: " + scholarshipTitle;
        String body = "Dear " + studentName + ",\n\n" +
                "We regret to inform you that your application for the \"" + scholarshipTitle
                + "\" scholarship has been rejected after manual audit.\n\n" +
                "Reason/Comments from Faculty Auditor:\n" +
                "\"" + (comments != null && !comments.trim().isEmpty() ? comments : "No remarks provided.") + "\"\n\n" +
                "If you believe this was an error, please verify your uploaded gradesheet and contact the administration.\n\n"
                +
                "Best regards,\n" +
                "Scholarly";

        printEmailBox(toEmail, subject, body);
    }

    private void printEmailBox(String to, String subject, String body) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n================================================================================");
        sb.append("\n[ SCHOLARLY TRANSACTIONAL EMAIL ]");
        sb.append("\n================================================================================");
        sb.append("\nTO:      ").append(to);
        sb.append("\nSUBJECT: ").append(subject);
        sb.append("\n--------------------------------------------------------------------------------");
        sb.append("\nBODY:");
        sb.append("\n").append(body);
        sb.append("\n================================================================================\n");
        log.info(sb.toString());
    }
}
