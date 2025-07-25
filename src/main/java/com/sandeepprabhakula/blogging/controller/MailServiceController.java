package com.sandeepprabhakula.blogging.controller;

import com.sandeepprabhakula.blogging.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin("*")
public class MailServiceController {
    @Autowired
    private EmailService emailService;
    @PostMapping("/send-mail")
    public ResponseEntity<?> sendEmail(@RequestParam("email") String email){
        return emailService.sendPasswordResetEmail(email);
    }
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleResponseStatusException(ResponseStatusException e) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", e.getStatusCode().value());
        body.put("message", e.getReason());
        return new ResponseEntity<>(body, e.getStatusCode());
    }
}
