package com.sandeepprabhakula.blogging.controller;

import com.sandeepprabhakula.blogging.service.EmailService;
import com.sandeepprabhakula.blogging.util.AppUtilities;
import jakarta.mail.MessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import org.springframework.http.server.reactive.ServerHttpRequest;
@RestController
@CrossOrigin("*")
public class MailServiceController {
    @Autowired
    private EmailService emailService;
    @Autowired
    private AppUtilities appUtilities;

    private final Logger log = LoggerFactory.getLogger(MailServiceController.class);
    @PostMapping("/send-mail")
    public Mono<ResponseEntity<?>> sendEmail(@RequestParam("email") String email,ServerHttpRequest request ) throws MessagingException {
        String firstIp = appUtilities.getClientIPAddr(request);
        log.info("Request from IP: {} to endpoint '/send-email", firstIp);
        log.info("Request initiated by: {}",email);
        return emailService.sendPasswordResetEmail(email);
    }

}
