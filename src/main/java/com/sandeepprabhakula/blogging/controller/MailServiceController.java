package com.sandeepprabhakula.blogging.controller;

import com.sandeepprabhakula.blogging.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
public class MailServiceController {
    @Autowired
    private EmailService emailService;
    @PostMapping("/send-mail")
    public ResponseEntity<?> sendEmail(@RequestParam("email") String email){
        return emailService.sendPasswordResetEmail(email);
    }
}
