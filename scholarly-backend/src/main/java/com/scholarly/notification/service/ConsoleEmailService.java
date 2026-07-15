package com.scholarly.notification.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ConsoleEmailService implements EmailService {

    private static final Logger log = LoggerFactory.getLogger(ConsoleEmailService.class);

    @Override
    public void sendApplicationSubmitted(String toEmail, String studentName, String scholarshipTitle) {
        printEmailBox(
            toEmail,
            "Scholarship Application Receipt: " + scholarshipTitle,
            "Dear " + studentName + ",\n\n" +
            "Your application for the \"" + scholarshipTitle + "\" scholarship has been successfully submitted.\n" +
            "It will be auto-processed and routed to our audit queue if manual verification is required.\n\n" +
            "You can track your application's review status directly on your Scholarly Dashboard.\n\n" +
            "Best regards,\n" +
            "Scholarly Scholarship Operations"
        );
    }

    @Override
    public void sendVerificationRequired(String toFacultyEmail, String studentName, String scholarshipTitle, String flagReason) {
        printEmailBox(
            toFacultyEmail,
            "Verification Required: " + scholarshipTitle + " (Student: " + studentName + ")",
            "Dear Faculty Auditor,\n\n" +
            "A scholarship application submitted by student \"" + studentName + "\" for the \"" + scholarshipTitle + "\" program requires manual audit.\n" +
            "Reason for audit: " + flagReason + "\n\n" +
            "Please log into the Scholarly Faculty Verification Panel to audit this student's gradesheet transcript.\n\n" +
            "Best regards,\n" +
            "Scholarly Automation Daemon"
        );
    }

    @Override
    public void sendApplicationApproved(String toEmail, String studentName, String scholarshipTitle, String comments) {
        printEmailBox(
            toEmail,
            "Scholarship Approved! Congratulations: " + scholarshipTitle,
            "Dear " + studentName + ",\n\n" +
            "Congratulations! Your application for the \"" + scholarshipTitle + "\" scholarship has been reviewed and APPROVED by the Faculty review panel.\n\n" +
            "Faculty Auditor Remarks:\n" +
            "\"" + (comments != null && !comments.trim().isEmpty() ? comments : "No remarks provided.") + "\"\n\n" +
            "Funding disbursement operations have been initialized.\n\n" +
            "Best regards,\n" +
            "Scholarly Scholarship Operations"
        );
    }

    @Override
    public void sendApplicationRejected(String toEmail, String studentName, String scholarshipTitle, String comments) {
        printEmailBox(
            toEmail,
            "Scholarship Application Update: " + scholarshipTitle,
            "Dear " + studentName + ",\n\n" +
            "We regret to inform you that your application for the \"" + scholarshipTitle + "\" scholarship has been rejected after manual audit.\n\n" +
            "Reason/Comments from Faculty Auditor:\n" +
            "\"" + (comments != null && !comments.trim().isEmpty() ? comments : "No remarks provided.") + "\"\n\n" +
            "If you believe this was an error, please verify your uploaded gradesheet and contact the scholastic administration.\n\n" +
            "Best regards,\n" +
            "Scholarly Scholarship Operations"
        );
    }

    private void printEmailBox(String to, String subject, String body) {
        StringBuilder box = new StringBuilder();
        box.append("\n┌────────────────────────────────────────────────────────────────────────────────────────┐");
        box.append("\n│                             [ SCHOLARLY TRANSACTIONAL EMAIL ]                          │");
        box.append("\n├────────────────────────────────────────────────────────────────────────────────────────┤");
        box.append("\n│ TO:      ").append(padRight(to, 76)).append("│");
        box.append("\n│ SUBJECT: ").append(padRight(subject, 76)).append("│");
        box.append("\n├────────────────────────────────────────────────────────────────────────────────────────┤");
        box.append("\n│ BODY:                                                                                  │");
        
        String[] lines = body.split("\n");
        for (String line : lines) {
            int start = 0;
            if (line.isEmpty()) {
                box.append("\n│   ").append(padRight("", 80)).append("│");
            } else {
                while (start < line.length()) {
                    int end = Math.min(start + 76, line.length());
                    String sub = line.substring(start, end);
                    box.append("\n│   ").append(padRight(sub, 80)).append("│");
                    start = end;
                }
            }
        }
        
        box.append("\n└────────────────────────────────────────────────────────────────────────────────────────┘\n");
        log.info(box.toString());
    }

    private String padRight(String s, int n) {
        if (s == null) return " ".repeat(n);
        if (s.length() >= n) return s.substring(0, n);
        return s + " ".repeat(n - s.length());
    }
}
