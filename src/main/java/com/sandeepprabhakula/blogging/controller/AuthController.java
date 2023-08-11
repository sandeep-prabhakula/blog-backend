package com.sandeepprabhakula.blogging.controller;

import com.sandeepprabhakula.blogging.data.User;
import com.sandeepprabhakula.blogging.dto.LoginDTO;
import com.sandeepprabhakula.blogging.service.JwtService;
import com.sandeepprabhakula.blogging.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@CrossOrigin("*")

public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;
    private final AuthenticationManager authMan;
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody User user){
        String response = userService.createNewUser(user);
        if(response.equals("User already exists"))return new ResponseEntity<>(HttpStatusCode.valueOf(409));
        return ResponseEntity.ok(response);
    }

//    @PostMapping("/login")
//    public User login(@RequestBody LoginDTO loginDTO){
//        return userService.login(loginDTO);
//    }

    @PostMapping("/authenticate")
    public String authenticate(@RequestBody LoginDTO loginDTO){
        Authentication auth = authMan.authenticate(new UsernamePasswordAuthenticationToken(loginDTO.getEmail(),loginDTO.getPassword()));
        if(auth.isAuthenticated())
            return jwtService.generateToken(loginDTO.getEmail());

        else return"Invalid User";
    }
}
