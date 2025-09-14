package com.chat.sr.mapper;

import com.chat.sr.dto.VetRequestDTO;
import com.chat.sr.dto.VetResponseDTO;
import com.chat.sr.model.User;
import com.chat.sr.model.Vet;
import org.springframework.stereotype.Component;

public class VetMapper {

    public static VetResponseDTO toDTO(Vet vet) {
        if (vet == null) return null;

        VetResponseDTO dto = new VetResponseDTO();
        dto.setId(vet.getId());
        dto.setDvmRegId(vet.getDvmRegId());
        dto.setUniversity(vet.getUniversity());
        dto.setResult(vet.getResult());
        dto.setPassingYear(vet.getPassingYear());
        dto.setSpecialization(vet.getSpecialization());

        if (vet.getUser() != null) {
            dto.setUserId(vet.getUser().getId());
            dto.setUserName(vet.getUser().getUserName());
        }

        return dto;
    }

    public static Vet toVet(VetRequestDTO dto, User user) {
        if (dto == null || user == null) return null;

        return Vet.builder()
                .dvmRegId(dto.getDvmRegId())
                .university(dto.getUniversity())
                .result(dto.getResult())
                .passingYear(dto.getPassingYear())
                .specialization(dto.getSpecialization())
                .user(user)
                .build();
    }
}
