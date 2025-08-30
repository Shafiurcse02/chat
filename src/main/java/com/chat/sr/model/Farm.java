package com.chat.sr.model;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "farms")
public class Farm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String farmName;

    @Column(nullable = false)
    private String location;

    // Owner সম্পর্ক
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)  // foreign key in farms table
    private User owner;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "farmType_id", nullable = false)  // foreign key in farms table
    private FarmType  farmType;
}

