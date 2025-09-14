package com.chat.sr.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Pet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private Owner owner;


   // @OneToMany(mappedBy = "pet")
    //private List<Appointment> appointments = new ArrayList<>();
}
