package com.chat.sr.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OwnerDTO {
    private Long id;
    private String firmName;
    private List<AppointmentDTO> appointments;
}
