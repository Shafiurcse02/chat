package com.chat.sr.controller;

import com.chat.sr.dto.LoginRequestDTO;
import com.chat.sr.dto.LoginRsponseDTO;
import com.chat.sr.dto.RegisterRequestDTO;
import com.chat.sr.dto.UserDTO;
import com.chat.sr.mapper.UserMapper;
import com.chat.sr.model.User;
import com.chat.sr.repo.UserRepository;
import com.chat.sr.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;
@RestController
// Optional, API prefix
public class UserController {
    @Autowired
    private UserRepository userRepository;
    @GetMapping("/user/my-profile")
    public ResponseEntity<UserDTO> getProfile(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        User user = userRepository.findByUserName(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(UserMapper.toDTO(user));
    }
    @PutMapping("/user/my-profile")
    public ResponseEntity<UserDTO> updateProfile(
            Authentication authentication,
            @RequestBody UserDTO userDTO
    ) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User user = userRepository.findByUserName(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));


        UserMapper.updateUserFromDTO(userDTO, user);
        userRepository.save(user);
        System.out.println("*********************Update*******************************");

        return ResponseEntity.ok(UserMapper.toDTO(user));
    }


}
