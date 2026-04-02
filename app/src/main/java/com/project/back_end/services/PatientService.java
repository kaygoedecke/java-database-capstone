package com.project.back_end.services;

import com.project.back_end.DTO.AppointmentDTO;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.PatientRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@org.springframework.stereotype.Service
public class PatientService {

    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final TokenService tokenService;

    public PatientService(PatientRepository patientRepository,
                          AppointmentRepository appointmentRepository,
                          TokenService tokenService) {
        this.patientRepository = patientRepository;
        this.appointmentRepository = appointmentRepository;
        this.tokenService = tokenService;
    }

    public int createPatient(Patient patient) {
        try {
            patientRepository.save(patient);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    @Transactional
    public ResponseEntity<Map<String, Object>> getPatientAppointment(Long id, String token) {
        Map<String, Object> response = new HashMap<>();

        try {
            String email = tokenService.extractEmail(token);
            Patient patient = patientRepository.findByEmail(email);

            if (patient == null || !patient.getId().equals(id)) {
                response.put("message", "Unauthorized access.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            List<Appointment> appointments = appointmentRepository.findByPatientId(id);
            List<AppointmentDTO> appointmentDTOs = appointments.stream()
                    .map(this::mapToDTO)
                    .toList();

            response.put("appointments", appointmentDTOs);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("message", "Failed to fetch patient appointments.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Transactional
    public ResponseEntity<Map<String, Object>> filterByCondition(String condition, Long id) {
        Map<String, Object> response = new HashMap<>();

        try {
            int status;

            if ("past".equalsIgnoreCase(condition)) {
                status = 1;
            } else if ("future".equalsIgnoreCase(condition)) {
                status = 0;
            } else {
                response.put("message", "Invalid condition. Use 'past' or 'future'.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            List<Appointment> appointments =
                    appointmentRepository.findByPatient_IdAndStatusOrderByAppointmentTimeAsc(id, status);

            List<AppointmentDTO> appointmentDTOs = appointments.stream()
                    .map(this::mapToDTO)
                    .toList();

            response.put("appointments", appointmentDTOs);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("message", "Failed to filter appointments by condition.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Transactional
    public ResponseEntity<Map<String, Object>> filterByDoctor(String name, Long patientId) {
        Map<String, Object> response = new HashMap<>();

        try {
            List<Appointment> appointments =
                    appointmentRepository.filterByDoctorNameAndPatientId(name, patientId);

            List<AppointmentDTO> appointmentDTOs = appointments.stream()
                    .map(this::mapToDTO)
                    .toList();

            response.put("appointments", appointmentDTOs);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("message", "Failed to filter appointments by doctor.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Transactional
    public ResponseEntity<Map<String, Object>> filterByDoctorAndCondition(String condition, String name, long patientId) {
        Map<String, Object> response = new HashMap<>();

        try {
            int status;

            if ("past".equalsIgnoreCase(condition)) {
                status = 1;
            } else if ("future".equalsIgnoreCase(condition)) {
                status = 0;
            } else {
                response.put("message", "Invalid condition. Use 'past' or 'future'.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            List<Appointment> appointments =
                    appointmentRepository.filterByDoctorNameAndPatientIdAndStatus(name, patientId, status);

            List<AppointmentDTO> appointmentDTOs = appointments.stream()
                    .map(this::mapToDTO)
                    .toList();

            response.put("appointments", appointmentDTOs);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("message", "Failed to filter appointments by doctor and condition.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    public ResponseEntity<Map<String, Object>> getPatientDetails(String token) {
        Map<String, Object> response = new HashMap<>();

        try {
            String email = tokenService.extractEmail(token);
            Patient patient = patientRepository.findByEmail(email);

            if (patient == null) {
                response.put("message", "Patient not found.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            response.put("patient", patient);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("message", "Failed to fetch patient details.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
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