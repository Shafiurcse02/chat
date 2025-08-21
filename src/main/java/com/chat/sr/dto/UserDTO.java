package com.chat.sr.dto;

import com.chat.sr.model.Role;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
public class UserDTO {
	private Long id;
    private String userName;
    private String email;
    private Role role;
    private boolean isActive;

}
