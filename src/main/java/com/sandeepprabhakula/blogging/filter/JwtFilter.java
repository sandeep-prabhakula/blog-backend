package com.sandeepprabhakula.blogging.filter;

import com.sandeepprabhakula.blogging.config.UserInfoUserDetailsService;
import com.sandeepprabhakula.blogging.service.JwtService;
import com.sandeepprabhakula.blogging.service.TokenBlackListService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class JwtFilter implements WebFilter {
    private final JwtService jwtService;
    private final TokenBlackListService blacklistService;
    private final UserInfoUserDetailsService userInfoUserDetails;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        return Mono.justOrEmpty(exchange.getRequest().getHeaders().getFirst("Authorization"))
                .filter(authHeader -> authHeader.startsWith("Bearer "))
                .flatMap(authHeader -> {
                    String token = authHeader.substring(7);
                    return processToken(token, exchange, chain);
                })
                .switchIfEmpty(Mono.defer(() -> chain.filter(exchange)));
    }

    private Mono<Void> processToken(String token, ServerWebExchange exchange, WebFilterChain chain) {
        return blacklistService.contains(token)
                .flatMap(isBlackListed -> {
                    if (isBlackListed) {
                        return writeErrorResponse(exchange, "Token revoked", HttpStatus.UNAUTHORIZED);
                    }

                    String username = jwtService.extractUsername(token);
                    if (username == null) {
                        return chain.filter(exchange);
                    }

                    return userInfoUserDetails.findByUsername(username)
                            .flatMap(userDetails -> {
                                if (jwtService.validateToken(token, userDetails)) {
                                    // ✅ FIXED: Properly set authentication in context
                                    UsernamePasswordAuthenticationToken authToken =
                                            new UsernamePasswordAuthenticationToken(
                                                    userDetails,
                                                    null,
                                                    userDetails.getAuthorities()
                                            );

                                    // ✅ CORRECT WAY: Use the chain with context modification
                                    return chain.filter(exchange)
                                            .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authToken));
                                }
                                return chain.filter(exchange);
                            })
                            .switchIfEmpty(chain.filter(exchange));
                });
    }


    private Mono<Void> writeErrorResponse(ServerWebExchange exchange, String message, HttpStatus status) {
        System.out.println("error while jwt validation.");
        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        String body = "{\"error\":\"" + message + "\"}";
        return exchange.getResponse().writeWith(Mono.just(exchange.getResponse()
                .bufferFactory()
                .wrap(body.getBytes())));
    }
}