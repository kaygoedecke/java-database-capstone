package com.project.back_end.controllers;

import com.project.back_end.models.Prescription;
import com.project.back_end.services.PrescriptionService;
import com.project.back_end.services.Service;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("${api.path}prescription")
public class PrescriptionController {

    private final PrescriptionService prescriptionService;
    private final Service service;

    public PrescriptionController(PrescriptionService prescriptionService, Service service) {
        this.prescriptionService = prescriptionService;
        this.service = service;
    }

    @PostMapping("/{token}")
    public ResponseEntity<Map<String, String>> savePrescription(@PathVariable String token,
                                                                @Valid @RequestBody Prescription prescription) {
        Map<String, String> validationResult = service.validateToken(token, "doctor");

        if (!validationResult.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(validationResult);
        }

        return prescriptionService.savePrescription(prescription);
    }

    @GetMapping("/{appointmentId}/{token}")
    public ResponseEntity<Map<String, Object>> getPrescription(@PathVariable Long appointmentId,
                                                               @PathVariable String token) {
        Map<String, String> validationResult = service.validateToken(token, "doctor");

        if (!validationResult.isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.putAll(validationResult);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        return prescriptionService.getPrescription(appointmentId);
    }
}