package com.chat.sr.controller;
import com.chat.sr.dto.LoginRequestDTO;
import com.chat.sr.dto.LoginRsponseDTO;
import com.chat.sr.dto.RegisterRequestDTO;
import com.chat.sr.exception.UserAlreadyExistsException;
import com.chat.sr.mapper.UserMapper;
import com.chat.sr.repo.UserRepository;
import com.chat.sr.service.AuthenticationService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
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
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequestDTO userDTO) {
        try {
            UserDTO savedUser = authenticationService.signup(userDTO);
            return ResponseEntity.ok(savedUser);
        } catch (UserAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already exists");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Something went wrong");
        }
    }

    // ------------------- Login -------------------
    @PostMapping("/login")
    public ResponseEntity<UserDTO> loginUser(@RequestBody LoginRequestDTO loginRequestDTO) {
        LoginRsponseDTO loginRsponseDTO = authenticationService.login(loginRequestDTO);

        // ✅ DB তে update
        userRepository.updateUserIsActiveStatus(loginRequestDTO.getUserName(), true);

        // ✅ OnlineUserService এও add
        ResponseCookie responseCookie = ResponseCookie.from("jwt", loginRsponseDTO.getToken())
                .httpOnly(true)
                .secure(false)
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
        if (authentication != null) {
            String username = authentication.getName();

            // ✅ DB তে offline
            userRepository.updateUserIsActiveStatus(username, false);

        }
        return authenticationService.logout(authentication);
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("******** ok *******");
    }
}
