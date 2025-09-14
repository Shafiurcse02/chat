package com.chat.sr.model;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "medicines")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Medicine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String dose;
    private String schedule;
    private String instructions;

    @ManyToOne
    @JoinColumn(name = "prescription_id")
    private Prescription prescription;
}
