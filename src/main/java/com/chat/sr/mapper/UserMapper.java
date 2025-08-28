package com.chat.sr.mapper;

import com.chat.sr.dto.UserDTO;
import com.chat.sr.model.User;

public class UserMapper {
    public static UserDTO toDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .userName(user.getUserName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole())
                .gender(user.getGender())
                .isActive(user.isActive())
                .build();
    }
}