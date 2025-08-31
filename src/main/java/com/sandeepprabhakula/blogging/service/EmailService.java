package com.sandeepprabhakula.blogging.service;

import com.sandeepprabhakula.blogging.repository.ReactiveUserRepository;
import jakarta.mail.MessagingException;
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
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    private String sender;
    private final ReactiveUserRepository userRepository;
    private final Logger log = LoggerFactory.getLogger(EmailService.class);

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
                """.formatted("https://codeverse-chronicles.vercel.app/_next/image?url=%2F_next%2Fstatic%2Fmedia%2Flogo.330cb985.png&w=96&q=75", resetLink, String.valueOf(now.getYear()));
    }

    public Mono<ResponseEntity<?>> sendPasswordResetEmail(String email) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        helper.setTo(email);

        return userRepository.findByEmail(email)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")))
                .flatMap(user -> {
                            String url = "https://codeverse-chronicles.vercel.app/setup-password?id=" + user.getId();
                            String htmlContent = getForgotPasswordHtmlContent(url);
                            try {
                                helper.setSubject("Password reset request");
                                helper.setText(htmlContent, true); // true = isHtml
                                helper.setFrom(sender);
                                mailSender.send(mimeMessage);
                                return Mono.just(ResponseEntity.ok()
                                        .body(Map.of("message", "Password reset email sent successfully")));
                            } catch (MessagingException e) {
                                throw new RuntimeException(e);
                            }


                        }

                );
    }
}
