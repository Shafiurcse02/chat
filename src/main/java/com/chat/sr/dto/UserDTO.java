package com.chat.sr.dto;

import com.chat.sr.model.Role;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
	private Long id;
    private String userName;
    private String email;
    private String phone;
    private String gender;
    private String district;
    private String photo;
    private String thana;
    private OwnerDTO owner; // Optional

    private String po;
    private Role role;
    private boolean isActive;


}


