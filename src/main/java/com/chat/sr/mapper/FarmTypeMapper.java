package com.chat.sr.mapper;

import com.chat.sr.dto.FarmTypeDTO;
import com.chat.sr.model.FarmType;

public class FarmTypeMapper {

    public static FarmTypeDTO toDTO(FarmType farmType) {
        return FarmTypeDTO.builder()
                .id(farmType.getId())
                .typeName(farmType.getTypeName())
                .build();
    }

    public static FarmType toEntity(FarmTypeDTO dto) {
        return FarmType.builder()
                .id(dto.getId())
                .typeName(dto.getTypeName())
                .build();
    }
}
