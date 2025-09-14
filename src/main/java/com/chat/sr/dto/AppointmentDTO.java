package com.chat.sr.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AppointmentDTO {
    private Long id;
    private String species;
    private String gender;
    private String age;
    private String description;
    private LocalDateTime appointmentDate;
    private Long ownerId;
    private Long vetId;
}
