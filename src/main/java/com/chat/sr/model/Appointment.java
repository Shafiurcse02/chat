package com.chat.sr.model;
import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "appointments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;
    private String gender;
    private String age;
    private String species;

    @CreationTimestamp
    @Column(updatable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime appointmentAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Dhaka")
    private LocalDateTime appointmentDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
@JsonBackReference
    private Owner owner;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vet_id")
@JsonBackReference
    private Vet vet;

    @OneToOne(mappedBy = "appointment", cascade = CascadeType.ALL)
    @JsonManagedReference
    private Prescription prescription;
}
