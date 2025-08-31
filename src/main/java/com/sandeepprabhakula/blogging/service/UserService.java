package com.sandeepprabhakula.blogging.service;

import com.sandeepprabhakula.blogging.data.User;
import com.sandeepprabhakula.blogging.repository.ReactiveUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor

public class UserService {

    private final ReactiveUserRepository userRepository;
    private final PasswordEncoder encoder;

    public Mono<ResponseEntity<Map<String, Object>>> createNewUser(User user) {
        return userRepository.findByEmail(user.getEmail())
                .flatMap(existingUser -> {
                    // ✅ User exists - return conflict response
                    Map<String, Object> response = new HashMap<>();
                    response.put("status", HttpStatus.CONFLICT.value());
                    response.put("message", "User already exists with this email");
                    return Mono.just(ResponseEntity.status(HttpStatus.CONFLICT).body(response));
                })
                .switchIfEmpty(Mono.defer(() -> {
                    // ✅ User doesn't exist - create new user
                    String encodedPassword = encoder.encode(user.getPassword());
                    user.setPassword(encodedPassword);

                    return userRepository.save(user)
                            .map(savedUser -> {
                                Map<String, Object> response = new HashMap<>();
                                response.put("status", HttpStatus.CREATED.value());
                                response.put("message", "Account created successfully");
                                response.put("userId", savedUser.getId());
                                return ResponseEntity.status(HttpStatus.CREATED).body(response);
                            });
                }))
                .onErrorResume(e -> {
                    // ✅ Error handling
                    Map<String, Object> errorResponse = new HashMap<>();
                    errorResponse.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
                    errorResponse.put("message", "Failed to create user: " + e.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse));
                });
    }

    public Mono<ResponseEntity<Map<String, Object>>> resetPassword(String id, String password) {
        return userRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")))
                .flatMap(user -> {
                    user.setPassword(encoder.encode(password));
                    return userRepository.save(user);
                })
                .map(savedUser ->
                {
                    Map<String, Object> response = new HashMap<>();
                    response.put("status", HttpStatus.OK.value());
                    response.put("message", "Password reset successful");
                    return new ResponseEntity<>(response, HttpStatus.OK);
                }).onErrorResume(e -> {
                    Map<String, Object> errorResponse = new HashMap<>();
                    errorResponse.put("status", HttpStatus.BAD_REQUEST.value());
                    errorResponse.put("message", e.getMessage());
                    return Mono.just(new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST));
                });

    }

    public Mono<ResponseEntity<User>> getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .flatMap(user -> Mono.just(new ResponseEntity<>(user, HttpStatus.OK)))
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")))
                .onErrorResume(e -> Mono.just(new ResponseEntity<>(new User(), HttpStatus.BAD_REQUEST)));
    }


}
