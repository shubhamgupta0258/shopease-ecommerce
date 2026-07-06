package com.example.e_commerce.controllers;

import com.example.e_commerce.dto.TokenRefreshRequest;
import com.example.e_commerce.payload.AuthRequest;
import com.example.e_commerce.payload.AuthResponse;
import com.example.e_commerce.payload.RegisterRequest;
import com.example.e_commerce.models.User;
import com.example.e_commerce.repositories.UserRepository;
import com.example.e_commerce.security.JwtUtil;
import com.example.e_commerce.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


    @RestController
    @RequestMapping("/api/auth")
    public class AuthController {

        @Autowired
        private AuthenticationManager authenticationManager;

        @Autowired
        private JwtUtil jwtUtil;

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private UserService userService;

        @Autowired
        private PasswordEncoder passwordEncoder;

        // Login endpoint
//        @PostMapping("/login")
//        public ResponseEntity<?> authenticateUser(@Valid @RequestBody AuthRequest authRequest) {
//            try {
//                Authentication authentication = authenticationManager.authenticate(
//                        new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword())
//                );
//
//
//                String jwt = jwtUtil.generateJwtToken(authentication);
//                return ResponseEntity.ok(new AuthResponse(jwt));
//            } catch (AuthenticationException e) {
//                return ResponseEntity.status(401).body("Invalid username or password");
//            }
//        }
        @PostMapping("/login")
        public ResponseEntity<?> authenticateUser(@Valid @RequestBody AuthRequest authRequest) {
            try {
                Authentication authentication = authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword())
                );

                String accessToken = jwtUtil.generateJwtToken(authentication);
                String refreshToken = jwtUtil.generateRefreshToken(authentication);

                return ResponseEntity.ok(new AuthResponse(accessToken, refreshToken));
            } catch (AuthenticationException e) {
                return ResponseEntity.status(401).body("Invalid username or password");
            }
        }


        // Refresh token endpoint - generates new access token
        @PostMapping("/refresh")
        public ResponseEntity<?> refreshToken(@RequestBody TokenRefreshRequest request) {
            String refreshToken = request.getRefreshToken();

            if (jwtUtil.validateJwtToken(refreshToken)) {
                String username = jwtUtil.getUsernameFromJwtToken(refreshToken);
                String newAccessToken = jwtUtil.generateJwtTokenFromUsername(username);
                // Optionally, generate a new refresh token or reuse the old one
                return ResponseEntity.ok(new AuthResponse(newAccessToken, refreshToken));
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid refresh token");
            }
        }


        // Returns the currently authenticated user's email + role
        @GetMapping("/me")
        public ResponseEntity<?> getCurrentUser(Authentication authentication) {
            String email = authentication.getName();
            User user = userRepository.findByEmail(email).orElse(null);

            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }

            return ResponseEntity.ok(Map.of("email", user.getEmail(), "role", user.getRole()));
        }

        // Register endpoint
        @PostMapping("/register")
        public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
            if (userService.existsByEmail(registerRequest.getEmail())) {
                return ResponseEntity.badRequest().body("Error: Email is already in use!");
            }

            User user = new User();
            user.setEmail(registerRequest.getEmail());
            user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
            // set other properties if needed
            System.out.println("Password to save: " + user.getPassword());
            userService.createUser(user);  // use createUser to save
            return ResponseEntity.ok("User registered successfully");
        }
    }
