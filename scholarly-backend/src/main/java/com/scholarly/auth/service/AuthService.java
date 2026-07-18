package com.scholarly.auth.service;

import com.scholarly.auth.dto.LoginRequest;
import com.scholarly.auth.dto.RegisterRequest;
import com.scholarly.auth.dto.UserResponse;
import com.scholarly.auth.model.Role;
import com.scholarly.auth.model.StudentProfile;
import com.scholarly.auth.model.User;
import com.scholarly.auth.repository.StudentProfileRepository;
import com.scholarly.auth.repository.UserRepository;
import com.scholarly.auth.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentProfileRepository studentProfileRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    // Container to hold user details along with generated token
    public record AuthResult(UserResponse user, String token) {
    }

    @Transactional
    public UserResponse register(RegisterRequest request) {
        // 1. Check if email is already taken
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email is already registered");
        }

        // 2. Build user object with hashed password
        User user = new User(
                request.email(),
                passwordEncoder.encode(request.password()),
                request.firstName(),
                request.lastName(),
                request.role());

        // 3. Handle student-specific details
        if (request.role() == Role.STUDENT) {
            validateStudentDetails(request);

            StudentProfile profile = new StudentProfile(
                    user,
                    request.gpa(),
                    request.department(),
                    request.enrollmentNumber());

            // Set bidirectional association
            user.setStudentProfile(profile);
        }

        // 4. Save entity (cascade will automatically save profile if student)
        User savedUser = userRepository.save(user);

        return mapToUserResponse(savedUser);
    }

    @Transactional(readOnly = true)
    public AuthResult login(LoginRequest request) {
        // 1. Find user by email
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new IllegalArgumentException("Pls first register yourself then log in"));

        // 2. Validate password match
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid email/password");
        }

        // 3. Generate JWT token
        String token = jwtUtils.generateToken(user);

        return new AuthResult(mapToUserResponse(user), token);
    }

    @Transactional(readOnly = true)
    public UserResponse getMe(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return mapToUserResponse(user);
    }

    private void validateStudentDetails(RegisterRequest request) {
        if (request.enrollmentNumber() != null && !request.enrollmentNumber().isBlank()) {
            if (studentProfileRepository.existsByEnrollmentNumber(request.enrollmentNumber())) {
                throw new IllegalArgumentException("Enrollment number is already registered");
            }
        }
    }

    private UserResponse mapToUserResponse(User user) {
        if (user.getRole() == Role.STUDENT && user.getStudentProfile() != null) {
            return new UserResponse(
                    user.getId(),
                    user.getEmail(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getRole(),
                    user.getStudentProfile().getGpa(),
                    user.getStudentProfile().getDepartment(),
                    user.getStudentProfile().getEnrollmentNumber());
        }
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole(),
                null,
                null,
                null);
    }
}
