package com.sandeepprabhakula.blogging.controller;

import com.sandeepprabhakula.blogging.data.User;
import com.sandeepprabhakula.blogging.dto.LoginDTO;
import com.sandeepprabhakula.blogging.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@CrossOrigin("*")
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody User user){
        String response = userService.createNewUser(user);
        if(response.equals("User already exists"))return new ResponseEntity<>(HttpStatusCode.valueOf(409));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public User login(@RequestBody LoginDTO loginDTO){
        return userService.login(loginDTO);
    }


}
