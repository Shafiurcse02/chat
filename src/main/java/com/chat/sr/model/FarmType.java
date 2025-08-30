package com.chat.sr.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "farm_types")
public class FarmType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String typeName;  // উদাহরণ: Poultry, Dairy, Fishery

    @OneToMany(mappedBy = "farmType", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Farm> farms;
}
