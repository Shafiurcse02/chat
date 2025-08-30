package com.chat.sr.service;

import com.chat.sr.dto.FarmTypeDTO;

import java.util.List;

public interface FarmTypeService {
    public List<FarmTypeDTO> getAllFarmTypes();
    public FarmTypeDTO createFarmType(FarmTypeDTO dto);
}
