package com.chat.sr.model;


import jakarta.persistence.*;
import lombok.*;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Medicine {

    private String name;
    private String dose;
    private String schedule;
    private String instructions;
}
