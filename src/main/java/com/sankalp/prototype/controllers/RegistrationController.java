package com.sankalp.prototype.controllers;

import com.sankalp.prototype.dtos.RegistrationRequest;
import com.sankalp.prototype.services.RegistrationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/registration")
public class RegistrationController {
    private final RegistrationService registrationService;
    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @PostMapping("/user")
    public ResponseEntity<Map<String, Object>> registerUser(@RequestBody RegistrationRequest requestDTO) {
        String procedureResponse = registrationService.registerUser(requestDTO);
        Map<String, Object> response = new HashMap<>();
        response.put("message", procedureResponse);

        if(procedureResponse != null && procedureResponse.startsWith("Invalid")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        return ResponseEntity.ok(response);
    }
}
