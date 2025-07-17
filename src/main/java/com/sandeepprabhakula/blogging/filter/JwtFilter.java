package com.sandeepprabhakula.blogging.filter;

import com.sandeepprabhakula.blogging.config.UserInfoUserDetailsService;
import com.sandeepprabhakula.blogging.service.JwtService;
import com.sandeepprabhakula.blogging.service.TokenBlackListService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.io.IOException;
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final TokenBlackListService blacklistService;
    private final UserInfoUserDetailsService userInfoUserDetails;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        String token;

        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);

                // 1. First check if token is blacklisted
                if (blacklistService.contains(token)) {
                    sendError(response, "Token revoked", HttpStatus.UNAUTHORIZED);
                    return;
                }

                String userName = jwtService.extractUsername(token);

                if (userName != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = userInfoUserDetails.loadUserByUsername(userName);

                    if (jwtService.validateToken(token, userDetails)) {
                        UsernamePasswordAuthenticationToken authToken =
                                new UsernamePasswordAuthenticationToken(
                                        userDetails,
                                        null,
                                        userDetails.getAuthorities());
                        authToken.setDetails(
                                new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    }
                }
            }

            filterChain.doFilter(request, response);

        } catch (ExpiredJwtException ex) {
            sendError(response, "Token expired", HttpStatus.UNAUTHORIZED);
        } catch (MalformedJwtException | SignatureException ex) {
            sendError(response, "Invalid token", HttpStatus.FORBIDDEN);
        } catch (Exception ex) {
            sendError(response, "Authentication failed", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void sendError(HttpServletResponse response, String message, HttpStatus status)
            throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(status.value());
        response.getWriter().write("{\"error\":\"" + message + "\"}");
    }
}