package com.chat.sr.mapper;

import com.chat.sr.dto.AppointmentDTO;
import com.chat.sr.model.Appointment;

public class AppointmentMapper {
    public static AppointmentDTO toDTO(Appointment appointment) {
        return AppointmentDTO.builder()
                .id(appointment.getId())
                .species(appointment.getSpecies())
                .gender(appointment.getGender())
                .age(appointment.getAge())
                .description(appointment.getDescription())
                .appointmentDate(appointment.getAppointmentDate())
                .ownerId(appointment.getOwner() != null ? appointment.getOwner().getId() : null)
                .vetId(appointment.getVet() != null ? appointment.getVet().getId() : null)
                .build();
    }
}
