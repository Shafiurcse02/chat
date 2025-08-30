package com.chat.sr.model;

import jakarta.annotation.Generated;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    @Column(nullable = false)
    private String phone;
    @Column(nullable = false)
    private String gender;
    private String district;
    private String thana;
    private String po;

    // এখানে enum ব্যবহার করা হল
    @Enumerated(EnumType.STRING)
    private Role role = Role.OWNER;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(name = "is_online", nullable = false)
    private boolean isActive = false;

    // Owner হলে farm সম্পর্ক


    //@OneToMany(mappedBy = "owner")
    //private List<Farm> farms;
}