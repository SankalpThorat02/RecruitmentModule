package com.sankalp.prototype.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);


    @Autowired
    private AuthService authService;

    @Autowired
    private ResponseCryptoService responseCryptoService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<?>> login(@RequestBody LoginRequest req, HttpServletRequest request) {

        log.info("[AUDIT] Login request received for email={}", req.getEmail());

        ApiResponse<LoginResponse> data = authService.login(req, request);

        return ResponseEntity.ok(
                encryptData(data)
        );

    }

    @PostMapping("/force-login")
    public ResponseEntity<ApiResponse<?>> forceLogin(@RequestParam Long userId, HttpServletRequest request) {
        log.info("[AUDIT] Force login request received for userId={}", userId);
        LoginResponse data = authService.forceLogin(userId, request);

        return ResponseEntity.ok(
                new ApiResponse<>("SUCCESS", "Force login successful", responseCryptoService.encryptData(data))
        );
    }