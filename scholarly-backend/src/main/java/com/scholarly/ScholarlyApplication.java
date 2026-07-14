package com.scholarly;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.io.File;

@SpringBootApplication
public class ScholarlyApplication {

    public static void main(String[] args) {
        SpringApplication.run(ScholarlyApplication.class, args);
    }

    @Bean
    public CommandLineRunner generateMockTranscripts() {
        return args -> {
            File matchFile = new File("c:/Users/User/Desktop/scholarly/transcript_match.pdf");
            File mismatchFile = new File("c:/Users/User/Desktop/scholarly/transcript_mismatch.pdf");

            if (!matchFile.exists()) {
                createPdf(matchFile, "8.85");
            }
            if (!mismatchFile.exists()) {
                createPdf(mismatchFile, "7.20");
            }
        };
    }

    private void createPdf(File file, String gpa) {
        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage();
            doc.addPage(page);
            try (PDPageContentStream contents = new PDPageContentStream(doc, page)) {
                contents.beginText();
                contents.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 12);
                contents.newLineAtOffset(100, 700);
                contents.showText("Official Academic Transcript");
                contents.newLineAtOffset(0, -30);
                contents.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
                contents.showText("Student Name: Pinky Shah");
                contents.newLineAtOffset(0, -20);
                contents.showText("Enrollment Number: CS2026001");
                contents.newLineAtOffset(0, -20);
                contents.showText("Department: Computer Science");
                contents.newLineAtOffset(0, -30);
                contents.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 12);
                contents.showText("Cumulative CGPA: " + gpa);
                contents.endText();
            }
            doc.save(file);
            System.out.println("Mock transcript generated at: " + file.getAbsolutePath());
        } catch (Exception e) {
            System.err.println("Failed to generate mock transcript PDF: " + e.getMessage());
        }
    }
}
