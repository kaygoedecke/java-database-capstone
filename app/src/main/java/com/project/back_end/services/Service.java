package com.project.back_end.services;

import com.project.back_end.DTO.Login;
import com.project.back_end.models.Admin;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AdminRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@org.springframework.stereotype.Service
public class Service {

    private final TokenService tokenService;
    private final AdminRepository adminRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final DoctorService doctorService;
    private final PatientService patientService;

    public Service(TokenService tokenService,
                   AdminRepository adminRepository,
                   DoctorRepository doctorRepository,
                   PatientRepository patientRepository,
                   DoctorService doctorService,
                   PatientService patientService) {
        this.tokenService = tokenService;
        this.adminRepository = adminRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.doctorService = doctorService;
        this.patientService = patientService;
    }

    public Map<String, String> validateToken(String token, String user) {
        Map<String, String> response = new HashMap<>();

        try {
            boolean valid = tokenService.validateToken(token, user);

            if (!valid) {
                response.put("message", "Invalid or expired token.");
            }
        } catch (Exception e) {
            response.put("message", "Invalid or expired token.");
        }

        return response;
    }

    public ResponseEntity<Map<String, String>> validateAdmin(Admin receivedAdmin) {
        Map<String, String> response = new HashMap<>();

        try {
            Admin admin = adminRepository.findByUsername(receivedAdmin.getUsername());

            if (admin == null) {
                response.put("message", "Admin not found.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            if (!admin.getPassword().equals(receivedAdmin.getPassword())) {
                response.put("message", "Invalid credentials.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            String token = tokenService.generateToken(admin.getUsername(), "admin");
            response.put("token", token);
            response.put("message", "Login successful.");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("message", "Internal server error.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    public Map<String, Object> filterDoctor(String name, String specialty, String time) {
        boolean hasName = name != null && !name.isBlank();
        boolean hasSpecialty = specialty != null && !specialty.isBlank();
        boolean hasTime = time != null && !time.isBlank();

        if (hasName && hasSpecialty && hasTime) {
            return doctorService.filterDoctorsByNameSpecilityandTime(name, specialty, time);
        } else if (hasName && hasSpecialty) {
            return doctorService.filterDoctorByNameAndSpecility(name, specialty);
        } else if (hasName && hasTime) {
            return doctorService.filterDoctorByNameAndTime(name, time);
        } else if (hasSpecialty && hasTime) {
            return doctorService.filterDoctorByTimeAndSpecility(specialty, time);
        } else if (hasName) {
            return doctorService.findDoctorByName(name);
        } else if (hasSpecialty) {
            return doctorService.filterDoctorBySpecility(specialty);
        } else if (hasTime) {
            return doctorService.filterDoctorsByTime(time);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("doctors", doctorService.getDoctors());
        return response;
    }

    public Map<String, String> validateAppointment(Appointment appointment) {
        Map<String, String> response = new HashMap<>();

        try {
            Optional<Doctor> doctorOptional = doctorRepository.findById(appointment.getDoctor().getId());

            if (doctorOptional.isEmpty()) {
                response.put("message", "Doctor not found.");
                return response;
            }

            LocalDateTime appointmentTime = appointment.getAppointmentTime();
            LocalDate date = appointmentTime.toLocalDate();
            LocalTime start = appointmentTime.toLocalTime();
            LocalTime end = start.plusHours(1);

            String requiredSlot = String.format("%02d:%02d-%02d:%02d",
                    start.getHour(), start.getMinute(),
                    end.getHour(), end.getMinute());

            List<String> availableSlots = doctorService.getDoctorAvailability(doctorOptional.get().getId(), date);

            if (!availableSlots.contains(requiredSlot)) {
                response.put("message", "Appointment time is unavailable.");
            }

            return response;

        } catch (Exception e) {
            response.put("message", "Failed to validate appointment.");
            return response;
        }
    }

    public boolean validatePatient(Patient patient) {
        Patient existingPatient = patientRepository.findByEmailOrPhone(patient.getEmail(), patient.getPhone());
        return existingPatient == null;
    }

    public ResponseEntity<Map<String, String>> validatePatientLogin(Login login) {
        Map<String, String> response = new HashMap<>();

        try {
            Patient patient = patientRepository.findByEmail(login.getIdentifier());

            if (patient == null) {
                response.put("message", "Patient not found.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            if (!patient.getPassword().equals(login.getPassword())) {
                response.put("message", "Invalid credentials.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            String token = tokenService.generateToken(patient.getEmail(), "patient");
            response.put("token", token);
            response.put("message", "Login successful.");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("message", "Internal server error.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    public ResponseEntity<Map<String, Object>> filterPatient(String condition, String name, String token) {
        try {
            String email = tokenService.extractEmail(token);
            Patient patient = patientRepository.findByEmail(email);

            if (patient == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Patient not found.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            boolean hasCondition = condition != null && !condition.isBlank();
            boolean hasName = name != null && !name.isBlank();

            if (hasCondition && hasName) {
                return patientService.filterByDoctorAndCondition(condition, name, patient.getId());
            } else if (hasCondition) {
                return patientService.filterByCondition(condition, patient.getId());
            } else if (hasName) {
                return patientService.filterByDoctor(name, patient.getId());
            } else {
                return patientService.getPatientAppointment(patient.getId(), token);
            }

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Failed to filter patient appointments.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}