package com.sandeepprabhakula.blogging.controller;

import com.sandeepprabhakula.blogging.service.EmailService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin("*")
public class MailServiceController {
    @Autowired
    private EmailService emailService;
    @PostMapping("/send-mail")
    public Mono<ResponseEntity<?>> sendEmail(@RequestParam("email") String email) throws MessagingException {
        return emailService.sendPasswordResetEmail(email);
    }

}
