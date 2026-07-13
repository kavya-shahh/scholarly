package com.scholarly.common.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;

@Service
public class FileStorageService {

    // Target folder inside workspace root (registered in gitignore)
    private final Path fileStorageLocation = Paths.get("c:/Users/User/Desktop/scholarly/uploads").toAbsolutePath().normalize();

    public FileStorageService() {
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    public String storeFile(MultipartFile file) {
        // 1. Check for empty files
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Failed to store empty file.");
        }

        // 2. Enforce strict format validations
        String contentType = file.getContentType();
        List<String> allowedTypes = List.of("application/pdf", "image/jpeg", "image/png");
        if (contentType == null || !allowedTypes.contains(contentType)) {
            throw new IllegalArgumentException("Invalid file format. Only PDF, JPEG, and PNG files are allowed.");
        }

        // 3. Extract original file extension
        String originalFileName = file.getOriginalFilename();
        String extension = "";
        if (originalFileName != null && originalFileName.contains(".")) {
            extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }

        // 4. Scramble file names using UUID to prevent naming collisions and security traversal
        String fileName = UUID.randomUUID().toString() + extension;

        try {
            // Copy input stream bytes directly to file block
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            // Return virtual resource URL matching our static config
            return "/uploads/" + fileName;
        } catch (IOException ex) {
            throw new RuntimeException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }
}
