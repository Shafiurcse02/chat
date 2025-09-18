package com.chat.sr.model;


import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "subscriptions")
public class Subscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // সম্পর্ক: একজন user এর multiple subscription থাকতে পারে
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    @JoinColumn(name = "owner_id", nullable = false)

    private Owner owner;

    @Enumerated(EnumType.STRING)
    private PlanType planType;  // BASIC, PREMIUM, TRIAL

    @Enumerated(EnumType.STRING)
    private SubscriptionStatus status; // ACTIVE, EXPIRED, CANCELED

    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate nextBillingDate;

    private Double fee;
}
