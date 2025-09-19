package com.sandeepprabhakula.blogging.controller;

import com.sandeepprabhakula.blogging.config.UserInfoUserDetails;
import com.sandeepprabhakula.blogging.data.User;
import com.sandeepprabhakula.blogging.dto.AuthResponse;
import com.sandeepprabhakula.blogging.dto.LoginDTO;
import com.sandeepprabhakula.blogging.dto.ResetPasswordDTO;
import com.sandeepprabhakula.blogging.service.JwtService;
import com.sandeepprabhakula.blogging.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@CrossOrigin("*")
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;
    private final ReactiveAuthenticationManager authMan;
    private final Logger log = LoggerFactory.getLogger(AuthController.class);

    @PostMapping("/register")
    public Mono<ResponseEntity<Map<String, Object>>> register(@RequestBody User user) {
        return userService.createNewUser(user);
    }

    @PostMapping("/authenticate")
    public Mono<ResponseEntity<?>> authenticate(@RequestBody LoginDTO loginDTO) {
        return authMan.authenticate(new UsernamePasswordAuthenticationToken(loginDTO.email(), loginDTO.password()))
                .flatMap(auth -> {
                    if (auth.isAuthenticated()) {
                        UserDetails userDetails = (UserDetails) auth.getPrincipal();
                        UserInfoUserDetails userInfoDetails = (UserInfoUserDetails) userDetails;
                        User user = userInfoDetails.getUser();
                        String token = jwtService.generateToken(loginDTO.email());
                        return Mono.just(ResponseEntity.ok(new AuthResponse(user, token)));

                    }
                    return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
                })
                .onErrorResume(e -> {
                    log.error("Authentication failed", e);
                    if(e instanceof UsernameNotFoundException){
                        return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error","Invalid email address or username")));
                    }
                    if(e instanceof BadCredentialsException){
                        return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error","Invalid password")));
                    }
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                });
    }

    @PostMapping("/reset-password")
    public Mono<ResponseEntity<Map<String, Object>>> resetPassword(@RequestBody ResetPasswordDTO resetPasswordDTO) {
        return userService.resetPassword(resetPasswordDTO.uid(), resetPasswordDTO.password());
    }


}
