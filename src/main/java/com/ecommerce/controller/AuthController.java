package com.ecommerce.controller;

import com.ecommerce.dto.AuthResponse;
import com.ecommerce.dto.LoginRequest;
import com.ecommerce.entity.User;
import com.ecommerce.service.UserService;
import com.ecommerce.utils.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    public AuthController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid User user) {
        User savedUser = userService.registerUser(user);
        return ResponseEntity.ok(
                new AuthResponse(null, "User registered successfully with role: " + savedUser.getRole())
        );
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequest request) {
        boolean valid = userService.validateUser(request.getEmail(), request.getPassword());
        if (!valid) return ResponseEntity.status(401)
                .body(new AuthResponse(null, "Invalid credentials"));

        User u = userService.getUserByEmail(request.getEmail());
        String token = jwtUtil.generateToken(u.getEmail(), u.getRole().name());

        return ResponseEntity.ok(new AuthResponse(token, "Login successful"));
    }
}
