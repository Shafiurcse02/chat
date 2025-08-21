package com.chat.sr.dto;

import lombok.Data;

@Data
public class LoginRsponseDTO {
	private String token;
	private UserDTO userDTO;
}
