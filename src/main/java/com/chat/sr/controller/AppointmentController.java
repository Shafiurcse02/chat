package com.chat.sr.controller;

import com.chat.sr.dto.CreateAppointmentRequest;
import com.chat.sr.model.Appointment;
import com.chat.sr.model.Owner;
import com.chat.sr.model.Vet;
import com.chat.sr.repo.AppointmentRepository;
import com.chat.sr.repo.OwnerRepository;
import com.chat.sr.repo.VetRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/appointment")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentRepository appointmentRepository;
    private final OwnerRepository ownerRepository;
    private final VetRepository vetRepository;

    // OWNER creates appointment
    @PreAuthorize("hasRole('OWNER')")
    @PostMapping("/create")
    public Appointment createAppointment(@RequestBody CreateAppointmentRequest createAppointmentRequest) {
        Owner owner = ownerRepository.findByUserId(createAppointmentRequest.getUserId()).orElseThrow(() -> new RuntimeException("Owner not found"));

        Appointment appointment = Appointment.builder()
                .owner(owner)
                .description(createAppointmentRequest.getDescription())
                .species(createAppointmentRequest.getSpecies())
                .build();
        return appointmentRepository.save(appointment);
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
}
