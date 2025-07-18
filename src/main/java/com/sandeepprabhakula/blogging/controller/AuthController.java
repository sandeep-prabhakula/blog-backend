package com.sandeepprabhakula.blogging.controller;

import com.sandeepprabhakula.blogging.data.User;
import com.sandeepprabhakula.blogging.dto.AuthResponse;
import com.sandeepprabhakula.blogging.dto.LoginDTO;
import com.sandeepprabhakula.blogging.dto.ResetPasswordDTO;
import com.sandeepprabhakula.blogging.service.JwtService;
import com.sandeepprabhakula.blogging.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@CrossOrigin("*")

public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;
    private final AuthenticationManager authMan;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody User user) {
        userService.createNewUser(user);
        return ResponseEntity.ok("Account created successfully.");
    }

    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticate(@RequestBody LoginDTO loginDTO) {
        Authentication auth = authMan.authenticate(new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getPassword()));
        ResponseEntity<?> response = userService.getUserByEmail(loginDTO.getEmail());
        Object responseBody = response.getBody();
        if (auth.isAuthenticated()) {
            return new ResponseEntity<>(new AuthResponse(responseBody,jwtService.generateToken(loginDTO.getEmail())), HttpStatus.OK);
        } else return new ResponseEntity<>(responseBody,HttpStatus.UNAUTHORIZED);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordDTO resetPasswordDTO) {
        return userService.resetPassword(resetPasswordDTO.getUid(), resetPasswordDTO.getPassword());
    }
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleResponseStatusException(ResponseStatusException e) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", e.getStatusCode().value());
        body.put("message", e.getReason());
        return new ResponseEntity<>(body, e.getStatusCode());
    }
}
