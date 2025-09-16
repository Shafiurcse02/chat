package com.chat.sr.service;

import com.chat.sr.dto.LoginRequestDTO;
import com.chat.sr.dto.LoginRsponseDTO;
import com.chat.sr.dto.RegisterRequestDTO;
import com.chat.sr.dto.UserDTO;
import com.chat.sr.exception.UserAlreadyExistsException;
import com.chat.sr.mapper.UserMapper;
import com.chat.sr.model.Owner;
import com.chat.sr.model.Role;
import com.chat.sr.model.User;
import com.chat.sr.repo.OwnerRepository;
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
    @Autowired
    private  OwnerRepository ownerRepository;


    public UserDTO signup(RegisterRequestDTO userDTO) {
        if (userRepository.findByUserName(userDTO.getUserName()).isPresent()) {
            throw new UserAlreadyExistsException("UserName Already Exists");
        }

        if (userRepository.findByEmail(userDTO.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("Email Already Exists");
        }
        // Convert DTO to entity
        User user = UserMapper.toUser(userDTO);
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setRole(Role.OWNER);

        // Create Owner and link bi-directionally
        Owner owner = Owner.builder().user(user).build();
        user.setOwner(owner);

        // Save user (cascade will save owner)
        User savedUser = userRepository.save(user);

        return UserMapper.toDTO(savedUser);
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
            System.out.println("🔴 User disconnected: " + userName);

            userRepository.updateUserIsActiveStatus(userName, false);
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
                .photo(user.getPhoto())
                .district(user.getDistrict())
                .thana(user.getThana())
                .po(user.getPo())
                .role(user.getRole())
                .isActive(user.isActive())
                .build();
    }
}
