package com.chat.sr.mapper;

import com.chat.sr.dto.RegisterRequestDTO;
import com.chat.sr.dto.UserDTO;
import com.chat.sr.model.Role;
import com.chat.sr.model.User;
import org.springframework.stereotype.Component;

public class UserMapper {

    // ✅ নতুন User তৈরি করার জন্য (Registration)
    public static User toUser(RegisterRequestDTO userDTO) {
        return User.builder()
                .userName(userDTO.getUserName())
                .email(userDTO.getEmail())
                .phone(userDTO.getPhone())
                .photo(userDTO.getPhoto())
                .gender(userDTO.getGender())
                .build();
    }

    // ✅ পুরনো User entity তে update করার জন্য (Profile Update)
    public static void updateUserFromDTO(UserDTO userDTO, User user) {
        if (userDTO.getPhone() != null) user.setPhone(userDTO.getPhone());
        if (userDTO.getGender() != null) user.setGender(userDTO.getGender());
        if (userDTO.getEmail() != null) user.setEmail(userDTO.getEmail());
        if (userDTO.getPhoto() != null) user.setPhoto(userDTO.getPhoto());
        if (userDTO.getDistrict() != null) user.setDistrict(userDTO.getDistrict());
        if (userDTO.getThana() != null) user.setThana(userDTO.getThana());
        if (userDTO.getPo() != null) user.setPo(userDTO.getPo());
        if (userDTO.getRole() != null) user.setRole(userDTO.getRole());
        // isActive শুধু admin update করতে পারবে, তাই চাইলে এখানে রাখবেন না
    }

    // ✅ Entity থেকে DTO বানানোর জন্য
    public static UserDTO toDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .userName(user.getUserName())
                .phone(user.getPhone())
                .gender(user.getGender())
                .photo(user.getPhoto())
                .email(user.getEmail())
                .district(user.getDistrict())
                .thana(user.getThana())
                .po(user.getPo())
                .role(user.getRole())
                .isActive(user.isActive())
                .build();
    }

}