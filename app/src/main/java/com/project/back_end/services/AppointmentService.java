package com.project.back_end.services;

import com.project.back_end.DTO.AppointmentDTO;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@org.springframework.stereotype.Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final TokenService tokenService;
    private final Service service;

    public AppointmentService(AppointmentRepository appointmentRepository,
                              PatientRepository patientRepository,
                              DoctorRepository doctorRepository,
                              TokenService tokenService,
                              Service service) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.tokenService = tokenService;
        this.service = service;
    }

    public int bookAppointment(Appointment appointment) {
        try {
            appointmentRepository.save(appointment);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    public ResponseEntity<Map<String, String>> updateAppointment(Appointment appointment) {
        Map<String, String> response = new HashMap<>();

        try {
            Optional<Appointment> existingAppointment = appointmentRepository.findById(appointment.getId());

            if (existingAppointment.isEmpty()) {
                response.put("message", "Appointment not found.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            Map<String, String> validationResult = service.validateAppointment(appointment);
            if (!validationResult.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(validationResult);
            }

            appointmentRepository.save(appointment);
            response.put("message", "Appointment updated successfully.");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("message", "Failed to update appointment.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    public ResponseEntity<Map<String, String>> cancelAppointment(long id, String token) {
        Map<String, String> response = new HashMap<>();

        try {
            Optional<Appointment> optionalAppointment = appointmentRepository.findById(id);

            if (optionalAppointment.isEmpty()) {
                response.put("message", "Appointment not found.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            Appointment appointment = optionalAppointment.get();

            String email = tokenService.extractEmail(token);
            Patient patient = patientRepository.findByEmail(email);

            if (patient == null) {
                response.put("message", "Patient not found.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            if (appointment.getPatient() == null || !appointment.getPatient().getId().equals(patient.getId())) {
                response.put("message", "You are not authorized to cancel this appointment.");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }

            appointmentRepository.delete(appointment);
            response.put("message", "Appointment cancelled successfully.");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("message", "Failed to cancel appointment.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    public Map<String, Object> getAppointment(String pname, LocalDate date, String token) {
        Map<String, Object> response = new HashMap<>();

        try {
            String email = tokenService.extractEmail(token);
            Doctor doctor = doctorRepository.findByEmail(email);

            if (doctor == null) {
                response.put("appointments", List.of());
                response.put("message", "Doctor not found.");
                return response;
            }

            LocalDateTime start = date.atStartOfDay();
            LocalDateTime end = date.plusDays(1).atStartOfDay().minusNanos(1);

            List<Appointment> appointments;

            if (pname == null || pname.isBlank() || "null".equalsIgnoreCase(pname)) {
                appointments = appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(doctor.getId(), start, end);
            } else {
                appointments = appointmentRepository
                        .findByDoctorIdAndPatient_NameContainingIgnoreCaseAndAppointmentTimeBetween(
                                doctor.getId(), pname, start, end
                        );
            }

            List<AppointmentDTO> appointmentDTOs = appointments.stream()
                    .map(this::mapToDTO)
                    .toList();

            response.put("appointments", appointmentDTOs);
            return response;

        } catch (Exception e) {
            response.put("appointments", List.of());
            response.put("message", "Failed to fetch appointments.");
            return response;
        }
    }

    private AppointmentDTO mapToDTO(Appointment appointment) {
        Doctor doctor = appointment.getDoctor();
        Patient patient = appointment.getPatient();

        return new AppointmentDTO(
                appointment.getId(),
                doctor != null ? doctor.getId() : null,
                doctor != null ? doctor.getName() : null,
                patient != null ? patient.getId() : null,
                patient != null ? patient.getName() : null,
                patient != null ? patient.getEmail() : null,
                patient != null ? patient.getPhone() : null,
                patient != null ? patient.getAddress() : null,
                appointment.getAppointmentTime(),
                appointment.getStatus()
        );
    }
}