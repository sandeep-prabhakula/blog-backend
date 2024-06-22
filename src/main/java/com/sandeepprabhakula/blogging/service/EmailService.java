package com.sandeepprabhakula.blogging.service;

import com.sandeepprabhakula.blogging.data.User;
import com.sandeepprabhakula.blogging.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    private String sender;
    private final UserRepository userRepository;

    public ResponseEntity<?> sendPasswordResetEmail(String email) {
        try {
            Optional<User> optionalUser = userRepository.findByEmail(email);
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                String url = "https://codeverse-chronicles.vercel.app/setup-password?id=" + user.getId();
                SimpleMailMessage mail = new SimpleMailMessage();
                mail.setFrom(sender);
                mail.setTo(email);
                mail.setText("Click the below link for setting up your password. \n " + url);
                mail.setSubject("Confirmation for password reset");
                mailSender.send(mail);
                Map<String, Object> map = new HashMap<>();
                map.put("status", 200);
                map.put("message", "Password reset email sent.");
                return new ResponseEntity<>(map, HttpStatus.OK);
            }
            return new ResponseEntity<>(new Exception("User Not Found"), HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
