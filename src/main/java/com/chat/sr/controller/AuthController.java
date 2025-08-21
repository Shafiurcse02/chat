package com.chat.sr.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import com.chat.sr.dto.UserDTO;
import com.chat.sr.model.User;
import com.chat.sr.security.JwtUtils;
import com.chat.sr.service.UserService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private AuthenticationManager authenticationManager;

    // ------------------- Registration -------------------
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserDTO userDTO) {
        if (userService.userExists(userDTO.getUserName())) {
            return ResponseEntity.badRequest().body("Username already exists");
        }

        User user = userService.createUser(userDTO); // service থেকে entity save হবে
        return ResponseEntity.ok("User registered successfully: " + user.getUserName());
    }

    // ------------------- Login -------------------
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody UserDTO userDTO) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(userDTO.getUserName(), userDTO.getPassword())
            );

            final User user = userService.findByUsername(userDTO.getUserName());
            final String token = jwtUtils.generateToken(userService.getUserDetails(user));

            return ResponseEntity.ok(new AuthResponse(token, user.getUserName(), user.getRole()));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(401).body("Invalid username or password");
        }
    }

    // ------------------- Auth Response DTO -------------------
    public static class AuthResponse {
        private String token;
        private String username;
        private Object role;

        public AuthResponse(String token, String username, Object role) {
            this.token = token;
            this.username = username;
            this.role = role;
        }

        public String getToken() { return token; }
        public String getUsername() { return username; }
        public Object getRole() { return role; }
    }
}
