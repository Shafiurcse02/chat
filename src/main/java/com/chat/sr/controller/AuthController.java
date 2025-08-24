package com.chat.sr.controller;
import com.chat.sr.dto.LoginRequestDTO;
import com.chat.sr.dto.LoginRsponseDTO;
import com.chat.sr.dto.RegisterRequestDTO;
import com.chat.sr.repo.UserRepository;
import com.chat.sr.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import com.chat.sr.dto.UserDTO;
import com.chat.sr.model.User;


@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private UserRepository userRepository;

    // ------------------- Registration -------------------
    @PostMapping("/register")
    public ResponseEntity<UserDTO> registerUser(@RequestBody RegisterRequestDTO userDTO) {
        return ResponseEntity.ok(authenticationService.signup(userDTO));
    }

    // ------------------- Login -------------------
    @PostMapping("/login")
    public ResponseEntity<UserDTO> loginUser(@RequestBody LoginRequestDTO loginRequestDTO) {
        LoginRsponseDTO loginRsponseDTO = authenticationService.login(loginRequestDTO);

        // ✅ Login হলে user online status true করে দেওয়া হলো
        userRepository.updateUserOnlineStatus(loginRequestDTO.getUserName(), true);

        ResponseCookie responseCookie = ResponseCookie.from("jwt", loginRsponseDTO.getToken())
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(1 * 60 * 60)
                .sameSite("strict")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .body(loginRsponseDTO.getUserDTO());
    }

    // ------------------- Logout -------------------
    @PostMapping("/logout")
    public ResponseEntity<String> logout(Authentication authentication) {
              return authenticationService.logout(authentication);
    }

    // ------------------- Current User -------------------
    @GetMapping("/getCurrentuser")
    public ResponseEntity<?> getCurrentuser(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("User Not Authorized");
        }

        String userName = authentication.getName();
        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return ResponseEntity.ok(convertToUserDTO(user));
    }

    private UserDTO convertToUserDTO(User user) {
        return UserDTO.builder()
                .userName(user.getUserName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }
}
