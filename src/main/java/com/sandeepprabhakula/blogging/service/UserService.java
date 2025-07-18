package com.sandeepprabhakula.blogging.service;

import com.sandeepprabhakula.blogging.data.User;
import com.sandeepprabhakula.blogging.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor

public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

    public void createNewUser(User user) {
        Optional<User> findingUser = userRepository.findByEmail(user.getEmail());
        if (findingUser.isPresent())  throw new ResponseStatusException(HttpStatus.CONFLICT, "User already exists");
        String password = user.getPassword();
        user.setPassword(encoder.encode(password));
        userRepository.save(user);
    }

    public ResponseEntity<?> resetPassword(String id, String password) {
        User user = userRepository.findById(id)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"User not found"));
        Map<String, Object> response = new HashMap<>();
        try{

            user.setPassword(encoder.encode(password));
            userRepository.save(user);
            response.put("status", 200);
            response.put("message", "Password reset successful");
            return new ResponseEntity<>(response, HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(new Exception(e.getMessage()),HttpStatus.BAD_REQUEST);
        }

    }

    public ResponseEntity<?> getUserByEmail(String email){
        try {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"User not found"));
            return new ResponseEntity<>(user,HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
