package com.chat.sr.controller;

import com.chat.sr.dto.AppointmentDTO;
import com.chat.sr.dto.CreateAppointmentRequest;
import com.chat.sr.mapper.AppointmentMapper;
import com.chat.sr.model.Appointment;
import com.chat.sr.model.Owner;
import com.chat.sr.model.Vet;
import com.chat.sr.repo.AppointmentRepository;
import com.chat.sr.repo.OwnerRepository;
import com.chat.sr.repo.VetRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/appointments")
@RequiredArgsConstructor
public class AppointmentController {
    private static final Logger logger = LoggerFactory.getLogger(AppointmentController.class);

    private final AppointmentRepository appointmentRepository;
    private final OwnerRepository ownerRepository;
    private final VetRepository vetRepository;

    // OWNER creates appointment
    @PreAuthorize("hasRole('OWNER')")
    @PostMapping("/create")
    public ResponseEntity<Appointment> createAppointment(
            @RequestBody CreateAppointmentRequest createAppointmentRequest) {

        Owner owner = ownerRepository.findByUserId(createAppointmentRequest.getUserId())
                .orElseThrow(() -> new RuntimeException("Owner not found"));

        Appointment appointment = Appointment.builder()
                .owner(owner)
                .description(createAppointmentRequest.getDescription())
                .species(createAppointmentRequest.getSpecies())
                .gender(createAppointmentRequest.getGender())
                .age(createAppointmentRequest.getAge())
                .appointmentDate(createAppointmentRequest.getAppointmentDate())
                .build();
        logger.info("Okk creat appointment {}", appointment);

        Appointment savedAppointment = appointmentRepository.save(appointment);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedAppointment);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('OWNER')")
    @DeleteMapping("/{appointmentId}")
    public ResponseEntity<?> deleteAppointment(@PathVariable Long appointmentId) {
        try {
            Appointment appointment = appointmentRepository.findById(appointmentId)
                    .orElseThrow(() -> new RuntimeException("Appointment not found"));

            appointmentRepository.delete(appointment);

            return ResponseEntity.ok("Appointment deleted successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete appointment");
        }
    }
    // OWNER or ADMIN can get appointment by ID
    @PreAuthorize("hasRole('OWNER') or hasRole('ADMIN')")
    @GetMapping("/{appointmentId}")
    public ResponseEntity<?> getAppointmentById(@PathVariable Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        return ResponseEntity.ok(AppointmentMapper.toDTO(appointment));
    }


    // ADMIN assigns vet
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{appointmentId}/assign-vet/{vetId}")
    public Appointment assignVet(@PathVariable Long appointmentId, @PathVariable Long vetId) {
        Appointment appointment = appointmentRepository.findById(appointmentId).orElseThrow(() -> new RuntimeException("Appointment not found"));
        Vet vet = vetRepository.findByUserId(vetId).orElseThrow(() -> new RuntimeException("Vet not found"));
        appointment.setVet(vet);
        return appointmentRepository.save(appointment);
    }

    // ADMIN or OWNER can fetch all appointments
    @PreAuthorize("hasRole('ADMIN') or hasRole('OWNER')")
    @GetMapping("/")
    public ResponseEntity<?> getAllAppointments() {
        try {
            List<Appointment> appointments = appointmentRepository.findAll();
            return ResponseEntity.ok(appointments);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to fetch appointments");
        }
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('OWNER')")
    @PutMapping("/{appointmentId}")
    public ResponseEntity<?> updateAppointmentById(
            @PathVariable Long appointmentId,
            @RequestBody CreateAppointmentRequest updateRequest) {

        try {
            Appointment appointment = appointmentRepository.findById(appointmentId)
                    .orElseThrow(() -> new RuntimeException("Appointment not found"));

            // Update fields if provided
            if (updateRequest.getSpecies() != null) appointment.setSpecies(updateRequest.getSpecies());
            if (updateRequest.getGender() != null) appointment.setGender(updateRequest.getGender());
            if (updateRequest.getAge() != null) appointment.setAge(updateRequest.getAge());
            if (updateRequest.getDescription() != null) appointment.setDescription(updateRequest.getDescription());
            if (updateRequest.getAppointmentDate() != null) appointment.setAppointmentDate(updateRequest.getAppointmentDate());

            Appointment updated = appointmentRepository.save(appointment);

            // Map to DTO
            AppointmentDTO dto = AppointmentMapper.toDTO(updated);
            return ResponseEntity.ok(dto);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update appointment");
        }
    }

    // OWNER or ADMIN can fetch appointments for a specific user
    @PreAuthorize("hasRole('ADMIN') or hasRole('OWNER')")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AppointmentDTO>> getAppointmentsByUserId(@PathVariable Long userId) {
        Owner owner = ownerRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Owner not found"));
logger.info("Check controller to fetch all appointmentys for a user id: {}",userId);
        List<AppointmentDTO> list = appointmentRepository.findByOwnerId(owner.getId())
                .stream()
                .map(AppointmentMapper::toDTO)
                .toList();

        return ResponseEntity.ok(list);
    }

    // OWNER or ADMIN can pay for an appointment
    @PreAuthorize("hasRole('OWNER') or hasRole('ADMIN')")
    @PostMapping("/{appointmentId}/pay")
    public ResponseEntity<?> payForAppointment(
            @PathVariable Long appointmentId,
            @RequestParam Double amount
    ) {

        return ResponseEntity.ok("Ok Payment");
    }




}
