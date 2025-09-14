package com.chat.sr.model;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
@Entity
@Table(name = "vets")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Vet {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String dvmRegId;
    private String university;
    private float result;
    private int passingYear;

    private String specialization;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "vet", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Appointment> appointments = new ArrayList<>();
}

