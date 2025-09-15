package com.chat.sr.model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

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

    @JsonIgnore
    @ToString.Exclude
    @OneToMany(mappedBy = "vet", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Appointment> appointments = new ArrayList<>();
}

