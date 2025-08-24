package com.chat.sr.dto;

import com.chat.sr.model.Role;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Data;
@Data
@Builder
public class RegisterRequestDTO {
    private String userName;
    private String password;
    private String email;
    private String role;
    private boolean isActive;
}

