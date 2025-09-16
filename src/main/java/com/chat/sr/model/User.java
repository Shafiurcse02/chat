package com.chat.sr.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.annotation.Generated;
import jakarta.persistence.*;
import lombok.*;
import org.apache.logging.log4j.util.Lazy;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    private String userName;
    @Column(nullable = false)
    private String password;
    private String phone;
    private String photo;
    @Column(nullable = false)
    private String gender;
    private String district;
    private String thana;
    private String po;
    // Relations for specific roles
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Owner owner;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Vet vet;

    // এখানে enum ব্যবহার করা হল
    @Enumerated(EnumType.STRING)
    private Role role = Role.OWNER;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(name = "is_online", nullable = false)
    private boolean isActive = false;


    @Column(name = "account_lock", nullable = false)
    private boolean approved = false;



}