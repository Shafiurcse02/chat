package com.chat.sr.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "appointment_payments")
public class AppointmentPayment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private Owner owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vet_id")
    private Vet vet;

    private LocalDateTime appointmentDate;
    private Double fee;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus; // PAID, FAILED, PENDING
}
