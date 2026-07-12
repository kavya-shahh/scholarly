package com.scholarly.auth.controller;

import com.scholarly.auth.dto.LoginRequest;
import com.scholarly.auth.dto.RegisterRequest;
import com.scholarly.auth.dto.UserResponse;
import com.scholarly.auth.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        UserResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(@Valid @RequestBody LoginRequest request, HttpServletResponse response) {
        AuthService.AuthResult result = authService.login(request);
        
        // Store JWT inside secure HTTP cookie (expires in 1 day)
        setJwtCookie(response, result.token(), 86400);

        return ResponseEntity.ok(result.user());
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        // Overwrite standard cookie with immediate expiry to destroy it
        setJwtCookie(response, "", 0);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMe() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal == null || "anonymousUser".equals(principal)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        String email = (String) principal;
        UserResponse response = authService.getMe(email);
        return ResponseEntity.ok(response);
    }

    private void setJwtCookie(HttpServletResponse response, String token, long maxAgeSec) {
        Cookie cookie = new Cookie("scholarly_token", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // In production, should be set to true for SSL/HTTPS
        cookie.setPath("/");
        cookie.setMaxAge((int) maxAgeSec);
        cookie.setAttribute("SameSite", "Strict"); // Crucial client defense against CSRF
        response.addCookie(cookie);
    }
}
