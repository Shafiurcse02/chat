package com.chat.sr.controller;

import com.chat.sr.dto.FarmTypeDTO;
import com.chat.sr.service.FarmTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class FarmTypeController {

    private final FarmTypeService farmTypeService;

    // ✅ সকল ইউজারের জন্য ওপেন
    @GetMapping("/auth/farm-types")
    public ResponseEntity<List<FarmTypeDTO>> getAllFarmTypes() {
        return ResponseEntity.ok(farmTypeService.getAllFarmTypes());
    }

    // ✅ শুধু ADMIN এর জন্য
    @PostMapping("/admin/addFarmType")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FarmTypeDTO> createFarmType(@RequestBody FarmTypeDTO dto) {
        return ResponseEntity.ok(farmTypeService.createFarmType(dto));
    }
}
