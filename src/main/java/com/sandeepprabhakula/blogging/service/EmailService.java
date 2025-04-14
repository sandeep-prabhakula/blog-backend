package com.sandeepprabhakula.blogging.service;

import com.sandeepprabhakula.blogging.controller.BlogController;
import com.sandeepprabhakula.blogging.data.User;
import com.sandeepprabhakula.blogging.repository.UserRepository;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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
    private final Logger log = LoggerFactory.getLogger(EmailService.class);

    public ResponseEntity<?> sendPasswordResetEmail(String email) {
        try {
            Optional<User> optionalUser = userRepository.findByEmail(email);
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                MimeMessage mimeMessage = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
                helper.setTo(email);
                String url = "https://codeverse-chronicles.vercel.app/setup-password?id=" + user.getId();
                String htmlContent = getForgotPasswordHtmlContent(url);
                helper.setSubject("Password reset request");
                helper.setText(htmlContent, true); // true = isHtml
                helper.setFrom(sender);
                mailSender.send(mimeMessage);
                log.info("Mail Sent");
                Map<String, Object> map = new HashMap<>();
                map.put("status", 200);
                map.put("message", "Password reset email sent.");
                return new ResponseEntity<>(map, HttpStatus.OK);
            }
            return new ResponseEntity<>(new Exception("User Not Found"), HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            log.error(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    private String getForgotPasswordHtmlContent(String resetLink) {
        LocalDate now = LocalDate.now();
        return """
                <!DOCTYPE html>
                <html>
                <head>
                  <style>
                    .email-container {
                      font-family: Arial, sans-serif;
                      background-color: #f4f4f4;
                      padding: 40px 20px;
                    }
                    .email-content {
                      max-width: 600px;
                      margin: auto;
                      background: #ffffff;
                      padding: 30px;
                      border-radius: 8px;
                      box-shadow: 0 2px 8px rgba(0,0,0,0.1);
                      text-align: center;
                    }
                    .logo {
                      height: 50px;
                      margin-bottom: 15px;
                    }
                    .app-name {
                      font-size: 24px;
                      font-weight: bold;
                      color: #2c3e50;
                      margin-bottom: 20px;
                    }
                    .greeting {
                      font-size: 18px;
                      margin-bottom: 25px;
                      color: #333333;
                    }
                    .reset-button {
                      display: inline-block;
                      background-color: #4CAF50;
                      color: white;
                      padding: 12px 24px;
                      font-size: 16px;
                      border-radius: 5px;
                      text-decoration: none;
                    }
                    .footer {
                      margin-top: 30px;
                      font-size: 13px;
                      color: #888888;
                    }
                  </style>
                </head>
                <body>
                  <div class="email-container">
                    <div class="email-content">
                      <img class="logo" src="%s" alt="Logo">
                      <div class="app-name">Codeverse Chronicles</div>
                      <div class="greeting">Hello,</div>
                      <div class="greeting">We received a request to reset your password.</div>
                      <a href="%s" class="reset-button">Reset Password</a>
                      <div class="footer">
                        If you didn't request this, please ignore this email.<br/>
                        © %s Codeverse Chronicles. All rights reserved.
                      </div>
                    </div>
                  </div>
                </body>
                </html>
                """.formatted("https://codeverse-chronicles.vercel.app/_next/image?url=%2F_next%2Fstatic%2Fmedia%2Flogo.330cb985.png&w=96&q=75",resetLink,String.valueOf(now.getYear()));
    }
}
