package com.chat.sr.service;

import com.chat.sr.dto.LoginRequestDTO;
import com.chat.sr.dto.LoginRsponseDTO;
import com.chat.sr.dto.RegisterRequestDTO;
import com.chat.sr.dto.UserDTO;
import com.chat.sr.model.Role;
import com.chat.sr.model.User;
import com.chat.sr.repo.UserRepository;
import com.chat.sr.security.CustomUserDetails;
import com.chat.sr.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AuthenticationService {
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtUtils jwtUtils;

    public UserDTO signup(RegisterRequestDTO userDTO) {
        if (userRepository.findByUserName(userDTO.getUserName()).isPresent()){
throw  new RuntimeException("UserName Already Exists");
        }
        userDTO.setActive(false);
        userDTO.setRole("USER");
        User user= User.builder()
                .userName(userDTO.getUserName())
                .email(userDTO.getEmail())
                .phone(userDTO.getPhone())
                .gender(userDTO.getGender())
                .password(passwordEncoder.encode(userDTO.getPassword()))
                .role(Role.valueOf(userDTO.getRole()))
                .build();
        User user1=userRepository.save(user);
        return convertToUserDTO(user1);
    }

    public LoginRsponseDTO login(LoginRequestDTO loginRequestDTO) {
        User user=userRepository.findByUserName(loginRequestDTO.getUserName()).orElseThrow(()-> new RuntimeException("User Not Found"));
       System.out.println(user);
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestDTO.getUserName(), loginRequestDTO.getPassword()));
       String token= jwtUtils.generateToken(new CustomUserDetails(user));
       return  LoginRsponseDTO.builder()
               .token(token)
               .userDTO(convertToUserDTO(user))
               .build();
    }

    public ResponseEntity<String> logout(Authentication authentication) {
        if (authentication != null) {
            String userName = authentication.getName();
            userRepository.updateUserOnlineStatus(userName, false);
        }
        ResponseCookie jwtCookie = ResponseCookie.from("jwt", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .sameSite("Strict")
                .build();

        SecurityContextHolder.clearContext();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .body("🚪 Logged out successfully");
    }



    private UserDTO convertToUserDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .userName(user.getUserName())
                .phone(user.getPhone())
                .gender(user.getGender())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }
}
