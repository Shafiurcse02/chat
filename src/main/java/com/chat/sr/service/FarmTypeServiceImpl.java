package com.chat.sr.service;

import com.chat.sr.dto.FarmTypeDTO;
import com.chat.sr.mapper.FarmTypeMapper;
import com.chat.sr.model.FarmType;
import com.chat.sr.repo.FarmTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FarmTypeServiceImpl implements FarmTypeService{
    @Autowired
    private FarmTypeRepository  farmTypeRepository;
    @Override
    public List<FarmTypeDTO> getAllFarmTypes() {
        return farmTypeRepository.findAll()
                .stream()
                .map(FarmTypeMapper::toDTO)
                .collect(Collectors.toList());    }

    @Override
    public FarmTypeDTO createFarmType(FarmTypeDTO dto) {
        FarmType farmType = FarmTypeMapper.toEntity(dto);
        FarmType saved = farmTypeRepository.save(farmType);
        return FarmTypeMapper.toDTO(saved);
    }
}
