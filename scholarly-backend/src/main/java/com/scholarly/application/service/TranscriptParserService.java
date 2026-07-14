package com.scholarly.application.service;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TranscriptParserService {

    private static final Logger log = LoggerFactory.getLogger(TranscriptParserService.class);

    // Prioritized Cumulative CGPA pattern: searches for final summary CGPA indicators
    private static final Pattern CUMULATIVE_GPA_PATTERN = Pattern.compile(
            "(?i)(?:cgpa|cumulative gpa|cumulative cgpa|cumulative grade point average|cumulative credit point average)\\D*([0-9]{1,2}(?:\\.[0-9]{1,2})?)"
    );

    // Fallback general GPA/SGPA pattern
    private static final Pattern GENERAL_GPA_PATTERN = Pattern.compile(
            "(?i)(?:gpa|sgpa|grade point average)\\D*([0-9]{1,2}(?:\\.[0-9]{1,2})?)"
    );

    public BigDecimal extractGpa(String relativeUrl) {
        try {
            // Translate the relative request path into a physical absolute path
            java.nio.file.Path filePath = java.nio.file.Paths.get("c:/Users/User/Desktop/scholarly", relativeUrl.substring(1)).toAbsolutePath().normalize();
            java.io.File file = filePath.toFile();

            if (!file.exists()) {
                log.error("Transcript file not found at local path: {}", filePath);
                return null;
            }

            String parsedText;
            try (PDDocument document = Loader.loadPDF(file)) {
                PDFTextStripper stripper = new PDFTextStripper();
                parsedText = stripper.getText(document);
            }

            if (parsedText == null || parsedText.trim().isEmpty()) {
                log.warn("Extracted text is empty. PDF might be a scanned image.");
                return null;
            }

            // 1. Scan text for Cumulative CGPA patterns (seeking final matches at summary sections)
            BigDecimal extractedGpa = scanTextForPattern(parsedText, CUMULATIVE_GPA_PATTERN);

            // 2. Fallback to general GPA markers if cumulative terms are missing
            if (extractedGpa == null) {
                extractedGpa = scanTextForPattern(parsedText, GENERAL_GPA_PATTERN);
            }

            if (extractedGpa != null) {
                log.info("Successfully extracted GPA from transcript: {}", extractedGpa);
                return extractedGpa.setScale(2, RoundingMode.HALF_UP);
            }

            log.warn("No GPA patterns resolved from transcript text.");
            return null;

        } catch (IOException e) {
            log.error("Failed to parse PDF transcript due to I/O error", e);
            return null;
        } catch (Exception e) {
            log.error("Unexpected error parsing transcript document", e);
            return null;
        }
    }

    private BigDecimal scanTextForPattern(String text, Pattern pattern) {
        Matcher matcher = pattern.matcher(text);
        BigDecimal lastMatch = null;

        // Loop through all matches to find the final summary record (located at bottom of page)
        while (matcher.find()) {
            try {
                String matchValue = matcher.group(1);
                BigDecimal gpaVal = new BigDecimal(matchValue);
                
                // Restrict results within logical bounds (0.0 to 10.0 scale)
                if (gpaVal.compareTo(BigDecimal.ZERO) >= 0 && gpaVal.compareTo(BigDecimal.TEN) <= 0) {
                    lastMatch = gpaVal;
                }
            } catch (NumberFormatException e) {
                // Ignore parse failures and continue scan
            }
        }
        return lastMatch;
    }
}
