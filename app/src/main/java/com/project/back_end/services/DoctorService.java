package com.project.back_end.services;

import com.project.back_end.DTO.Login;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final TokenService tokenService;

    public DoctorService(DoctorRepository doctorRepository,
                         AppointmentRepository appointmentRepository,
                         TokenService tokenService) {
        this.doctorRepository = doctorRepository;
        this.appointmentRepository = appointmentRepository;
        this.tokenService = tokenService;
    }

    @Transactional
    public List<String> getDoctorAvailability(Long doctorId, LocalDate date) {
        Optional<Doctor> optionalDoctor = doctorRepository.findById(doctorId);

        if (optionalDoctor.isEmpty()) {
            return List.of();
        }

        Doctor doctor = optionalDoctor.get();
        List<String> allSlots = doctor.getAvailableTimes() != null
                ? new ArrayList<>(doctor.getAvailableTimes())
                : new ArrayList<>();

        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay().minusNanos(1);

        List<Appointment> appointments =
                appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(doctorId, start, end);

        Set<String> bookedSlots = appointments.stream()
                .map(appointment -> formatSlot(appointment.getAppointmentTime()))
                .collect(Collectors.toSet());

        return allSlots.stream()
                .filter(slot -> !bookedSlots.contains(slot))
                .collect(Collectors.toList());
    }

    public int saveDoctor(Doctor doctor) {
        try {
            Doctor existingDoctor = doctorRepository.findByEmail(doctor.getEmail());
            if (existingDoctor != null) {
                return -1;
            }

            doctorRepository.save(doctor);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    public int updateDoctor(Doctor doctor) {
        try {
            Optional<Doctor> existingDoctor = doctorRepository.findById(doctor.getId());

            if (existingDoctor.isEmpty()) {
                return -1;
            }

            doctorRepository.save(doctor);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    @Transactional
    public List<Doctor> getDoctors() {
        return doctorRepository.findAll();
    }

    public int deleteDoctor(long id) {
        try {
            Optional<Doctor> existingDoctor = doctorRepository.findById(id);

            if (existingDoctor.isEmpty()) {
                return -1;
            }

            appointmentRepository.deleteAllByDoctorId(id);
            doctorRepository.deleteById(id);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    public ResponseEntity<Map<String, String>> validateDoctor(Login login) {
        Map<String, String> response = new HashMap<>();

        try {
            Doctor doctor = doctorRepository.findByEmail(login.getIdentifier());

            if (doctor == null) {
                response.put("message", "Doctor not found.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            if (!doctor.getPassword().equals(login.getPassword())) {
                response.put("message", "Invalid credentials.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            String token = tokenService.generateToken(doctor.getEmail(), "doctor");

            response.put("token", token);
            response.put("message", "Login successful.");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("message", "Internal server error.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Transactional
    public Map<String, Object> findDoctorByName(String name) {
        Map<String, Object> response = new HashMap<>();
        List<Doctor> doctors = doctorRepository.findByNameLike(name == null ? "" : name);
        response.put("doctors", doctors);
        return response;
    }

    @Transactional
    public Map<String, Object> filterDoctorsByNameSpecilityandTime(String name, String specialty, String timeFilter) {
        Map<String, Object> response = new HashMap<>();
        List<Doctor> doctors = doctorRepository.findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(
                name == null ? "" : name,
                specialty == null ? "" : specialty
        );
        response.put("doctors", filterDoctorByTime(doctors, timeFilter));
        return response;
    }

    @Transactional
    public Map<String, Object> filterDoctorByNameAndTime(String name, String timeFilter) {
        Map<String, Object> response = new HashMap<>();
        List<Doctor> doctors = doctorRepository.findByNameLike(name == null ? "" : name);
        response.put("doctors", filterDoctorByTime(doctors, timeFilter));
        return response;
    }

    @Transactional
    public Map<String, Object> filterDoctorByNameAndSpecility(String name, String specialty) {
        Map<String, Object> response = new HashMap<>();
        List<Doctor> doctors = doctorRepository.findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(
                name == null ? "" : name,
                specialty == null ? "" : specialty
        );
        response.put("doctors", doctors);
        return response;
    }

    @Transactional
    public Map<String, Object> filterDoctorByTimeAndSpecility(String specialty, String timeFilter) {
        Map<String, Object> response = new HashMap<>();
        List<Doctor> doctors = doctorRepository.findBySpecialtyIgnoreCase(specialty);
        response.put("doctors", filterDoctorByTime(doctors, timeFilter));
        return response;
    }

    @Transactional
    public Map<String, Object> filterDoctorBySpecility(String specialty) {
        Map<String, Object> response = new HashMap<>();
        List<Doctor> doctors = doctorRepository.findBySpecialtyIgnoreCase(specialty);
        response.put("doctors", doctors);
        return response;
    }

    @Transactional
    public Map<String, Object> filterDoctorsByTime(String timeFilter) {
        Map<String, Object> response = new HashMap<>();
        List<Doctor> doctors = doctorRepository.findAll();
        response.put("doctors", filterDoctorByTime(doctors, timeFilter));
        return response;
    }

    private List<Doctor> filterDoctorByTime(List<Doctor> doctors, String timeFilter) {
        if (timeFilter == null || timeFilter.isBlank()) {
            return doctors;
        }

        return doctors.stream()
                .filter(doctor -> doctor.getAvailableTimes() != null
                        && doctor.getAvailableTimes().stream().anyMatch(slot -> {
                    if (slot.equalsIgnoreCase(timeFilter)) {
                        return true;
                    }

                    try {
                        String startTime = slot.split("-")[0];
                        LocalTime time = LocalTime.parse(startTime);

                        if ("AM".equalsIgnoreCase(timeFilter)) {
                            return time.getHour() < 12;
                        } else if ("PM".equalsIgnoreCase(timeFilter)) {
                            return time.getHour() >= 12;
                        }
                    } catch (Exception ignored) {
                    }

                    return false;
                }))
                .collect(Collectors.toList());
    }

    private String formatSlot(LocalDateTime appointmentTime) {
        LocalTime start = appointmentTime.toLocalTime();
        LocalTime end = start.plusHours(1);

        return String.format("%02d:%02d-%02d:%02d",
                start.getHour(), start.getMinute(),
                end.getHour(), end.getMinute());
    }
}