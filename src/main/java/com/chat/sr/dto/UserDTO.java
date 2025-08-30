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
public class UserDTO {
	private Long id;
    private String userName;
    private String email;
    private String phone;
    private String gender;
    private String district;
    private String thana;
    private String po;
    private Role role;
    private boolean isActive;

}
