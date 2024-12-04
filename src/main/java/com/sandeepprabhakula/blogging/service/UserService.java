package com.sandeepprabhakula.blogging.service;

import com.sandeepprabhakula.blogging.data.User;
import com.sandeepprabhakula.blogging.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor

public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

    public String createNewUser(User user) {
        Optional<User> findingUser = userRepository.findByEmail(user.getEmail());

        if (findingUser.isPresent()) return "User already exists";
        String password = user.getPassword();
        user.setPassword(encoder.encode(password));
        userRepository.save(user);
        return "Account Creation Successful";
    }

    public ResponseEntity<?> resetPassword(String id, String password) {
        User user = userRepository.findById(id).get();
        Map<String, Object> response = new HashMap<>();
        try{

            if (user != null) {
                user.setPassword(encoder.encode(password));
                userRepository.save(user);
                response.put("status", 200);
                response.put("message", "Password reset successful");
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
            return new ResponseEntity<>(new Exception("User not found"),HttpStatus.NO_CONTENT);
        }catch (Exception e){
            return new ResponseEntity<>(new Exception(e.getMessage()),HttpStatus.BAD_REQUEST);
        }

    }

    public ResponseEntity<?> getUserByEmail(String email){
        try {
            Optional<User> optionalUser = userRepository.findByEmail(email);
            Map<String,Object>responseMap = new HashMap<>();

            if(optionalUser.isEmpty()) {
                responseMap.put("error","Invalid Credentials");
                responseMap.put("message","No user found with email "+email);
                return new ResponseEntity<>(responseMap, HttpStatus.BAD_REQUEST);
            }
            else{
                return new ResponseEntity<>(optionalUser.get(),HttpStatus.OK);
            }
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
