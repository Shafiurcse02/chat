package com.chat.sr.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginRsponseDTO {
	private String token;
	private UserDTO userDTO;
}
