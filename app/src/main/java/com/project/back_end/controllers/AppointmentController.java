package com.project.back_end.controllers;

import com.project.back_end.models.Appointment;
import com.project.back_end.services.AppointmentService;
import com.project.back_end.services.Service;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final Service service;

    public AppointmentController(AppointmentService appointmentService, Service service) {
        this.appointmentService = appointmentService;
        this.service = service;
    }

    @GetMapping("/{date}/{patientName}/{token}")
    public ResponseEntity<Map<String, Object>> getAppointments(@PathVariable String date,
                                                               @PathVariable String patientName,
                                                               @PathVariable String token) {
        Map<String, String> validationResult = service.validateToken(token, "doctor");

        if (!validationResult.isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.putAll(validationResult);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        LocalDate localDate = LocalDate.parse(date);
        Map<String, Object> appointments = appointmentService.getAppointment(patientName, localDate, token);
        return ResponseEntity.ok(appointments);
    }

    @PostMapping("/{token}")
    public ResponseEntity<Map<String, String>> bookAppointment(@PathVariable String token,
                                                               @Valid @RequestBody Appointment appointment) {
        Map<String, String> validationResult = service.validateToken(token, "patient");

        if (!validationResult.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(validationResult);
        }

        Map<String, String> appointmentValidation = service.validateAppointment(appointment);
        if (!appointmentValidation.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(appointmentValidation);
        }

        int result = appointmentService.bookAppointment(appointment);

        Map<String, String> response = new HashMap<>();
        if (result == 1) {
            response.put("message", "Appointment booked successfully.");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }

        response.put("message", "Failed to book appointment.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @PutMapping("/{token}")
    public ResponseEntity<Map<String, String>> updateAppointment(@PathVariable String token,
                                                                 @Valid @RequestBody Appointment appointment) {
        Map<String, String> validationResult = service.validateToken(token, "patient");

        if (!validationResult.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(validationResult);
        }

        return appointmentService.updateAppointment(appointment);
    }

    @DeleteMapping("/{id}/{token}")
    public ResponseEntity<Map<String, String>> cancelAppointment(@PathVariable long id,
                                                                 @PathVariable String token) {
        Map<String, String> validationResult = service.validateToken(token, "patient");

        if (!validationResult.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(validationResult);
        }

        return appointmentService.cancelAppointment(id, token);
    }
}