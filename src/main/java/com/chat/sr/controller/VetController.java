package com.chat.sr.controller;

import com.chat.sr.dto.VetRequestDTO;
import com.chat.sr.dto.VetResponseDTO;
import com.chat.sr.mapper.VetMapper;
import com.chat.sr.model.Role;
import com.chat.sr.model.User;
import com.chat.sr.model.Vet;

import com.chat.sr.repo.UserRepository;
import com.chat.sr.repo.VetRepository;
import com.chat.sr.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/vets")
@RequiredArgsConstructor
public class VetController {

    private final VetRepository vetRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    // ✅ Create Vet Profile (if user role is VET)
    @PostMapping("/create")
    @PreAuthorize("hasRole('VET')")
    public ResponseEntity<?> createVet(@RequestBody VetRequestDTO vetRequestDTO) {
        Optional<User> userOpt = userRepository.findById(vetRequestDTO.getUserId());
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found");
        }

        User user = userOpt.get();
        if (user.getRole() != Role.VET) {
            return ResponseEntity.status(403).body("User is not a VET");
        }

        // Check if vet already exists
        if (vetRepository.findByUserId(user.getId()).isPresent()) {
            return ResponseEntity.badRequest().body("Vet profile already exists");
        }

        Vet vet = VetMapper.toVet(vetRequestDTO, user);
        Vet savedVet = vetRepository.save(vet);
        return ResponseEntity.ok(VetMapper.toDTO(savedVet));
    }

    @PreAuthorize("hasAnyRole('VET','ADMIN')")
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getVetByUserId(@PathVariable Long userId) {
        // প্রথমে user বের করা হলো
        User user = userService.getUserByUId(userId);
        if (user == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("❌ ইউজার পাওয়া যায়নি, userId: " + userId);
        }

        // এবার vet খোঁজা হবে user এর সাথে সম্পর্কিত
        Vet vet = vetRepository.findByUserId(user.getId()).get();

        if (vet == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("❌ এই userId: " + userId + " এর জন্য কোনো Vet profile পাওয়া যায়নি।");
        }

        // যদি vet পাওয়া যায়, তাহলে DTO তে রূপান্তর করে response ফেরত দেওয়া
        VetResponseDTO dto = VetMapper.toDTO(vet);

        return ResponseEntity.ok(dto);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<?> getAllVets() {
        return ResponseEntity.ok(vetRepository.findAll());
    }


    @PreAuthorize("hasRole('VET')")
    @GetMapping("/me")
    public ResponseEntity<?> getMyVetProfile(Authentication authentication) {
        // বর্তমানে লগইন করা ইউজারের username বের করা
        String username = authentication.getName();
        Optional<User> userOptional = userRepository.findByUserName(username);
        if (userOptional.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("❌ User পাওয়া যায়নি: " + username);
        }
        User user = userOptional.get();

        // ওই user এর সাথে সম্পর্কিত Vet profile খোঁজা
        Optional<Vet> vetOptional = vetRepository.findByUserId(user.getId());
        if (vetOptional.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("❌ Vet profile পাওয়া যায়নি এই ইউজারের জন্য: " + username);
        }
        Vet vet = vetOptional.get();

        // Vet profile DTO তে রূপান্তর করে response ফেরত দেওয়া
        VetResponseDTO dto = VetMapper.toDTO(vet);

        return ResponseEntity.ok(dto);
    }



    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{vetId}")
    public ResponseEntity<?> deleteVet(@PathVariable Long vetId) {
        if (!vetRepository.existsById(vetId)) {
            return ResponseEntity.notFound().build();
        }
        vetRepository.deleteById(vetId);
        return ResponseEntity.ok("Vet deleted");
    }

}
