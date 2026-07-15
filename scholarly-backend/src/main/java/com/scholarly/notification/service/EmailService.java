package com.scholarly.notification.service;

public interface EmailService {
    void sendApplicationSubmitted(String toEmail, String studentName, String scholarshipTitle);
    void sendVerificationRequired(String toFacultyEmail, String studentName, String scholarshipTitle, String flagReason);
    void sendApplicationApproved(String toEmail, String studentName, String scholarshipTitle, String comments);
    void sendApplicationRejected(String toEmail, String studentName, String scholarshipTitle, String comments);
}
