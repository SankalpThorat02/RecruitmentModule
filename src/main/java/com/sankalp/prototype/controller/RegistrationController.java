package com.sankalp.prototype.controller;

import com.sankalp.prototype.dto.RegistrationRequest;
import com.sankalp.prototype.service.RegistrationService;
import com.sankalp.prototype.util.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/register")
public class RegistrationController {

    @Autowired
    private RegistrationService registrationService;

    @PostMapping("/user")
    public ResponseEntity<ApiResponse<?>> registerUser(@RequestBody RegistrationRequest requestDTO) {

        String procedureResponse = registrationService.registerUser(requestDTO);

        //If procedure returns invalid message, return badRequest
        if(procedureResponse != null && procedureResponse.startsWith("Invalid")) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("ERROR", procedureResponse, null));
        }

        return ResponseEntity.ok(new ApiResponse<>("SUCCESS", procedureResponse, null));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<?>> handleRuntime(RuntimeException ex) {

        return ResponseEntity.badRequest().body(new ApiResponse<>("ERROR", ex.getMessage(), null));
    }
}