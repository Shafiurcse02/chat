package com.chat.sr.controller;

import com.chat.sr.model.Appointment;
import com.chat.sr.model.Medicine;
import com.chat.sr.model.Prescription;
import com.chat.sr.repo.AppointmentRepository;
import com.chat.sr.repo.PrescriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/prescribe")
@RequiredArgsConstructor
public class PrescriptionController {

    private final PrescriptionRepository prescriptionRepository;
    private final AppointmentRepository appointmentRepository;

    // Create Prescription for an Appointment


    @PreAuthorize("hasRole('VET')")
    @PostMapping
    public Prescription createPrescription(@RequestBody PrescriptionRequest request) {

        Appointment appointment = appointmentRepository.findById(request.getAppointmentId())
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        Prescription prescription = Prescription.builder()
                .notes(request.getNotes())
                .appointment(appointment)
                .build();

        // Set Prescription in medicines
        List<Medicine> medicines = request.getMedicines();
        for (Medicine m : medicines) {
            m.setPrescription(prescription);
        }
        prescription.setMedicines(medicines);

        return prescriptionRepository.save(prescription);
    }

    // DTO for request
    @lombok.Data
    public static class PrescriptionRequest {
        private Long appointmentId;
        private String notes;
        private List<Medicine> medicines;
    }
}
