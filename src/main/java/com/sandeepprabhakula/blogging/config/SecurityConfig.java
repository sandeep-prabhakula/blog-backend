package com.sandeepprabhakula.blogging.config;

import com.sandeepprabhakula.blogging.filter.JwtFilter;
import com.sandeepprabhakula.blogging.service.TokenBlackListService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import reactor.core.publisher.Mono;

import java.util.List;

@EnableWebFluxSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtFilter jwtFilter;
    private final TokenBlackListService tokenBlackListService;
    private final Logger log = LoggerFactory.getLogger(SecurityConfig.class);
    private final ReactiveUserDetailsService userDetailsService;


    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity httpSecurity) {
        return httpSecurity
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint((exchange, ex) -> {
                            if (exchange.getResponse().isCommitted()) {
                                return Mono.empty(); // ✅ Skip if response already committed
                            }
                            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                            return exchange.getResponse().setComplete();
                        })
                        .accessDeniedHandler((exchange, denied) -> {
                            if (exchange.getResponse().isCommitted()) {
                                return Mono.empty(); // ✅ Skip if response already committed
                            }
                            exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                            return exchange.getResponse().setComplete();
                        })
                )
                .authorizeExchange(exchanges -> exchanges
                        // ✅ PUBLIC ENDPOINTS - No authentication required
                        .pathMatchers(

                                "/get-all-blogs",
                                "/blog/**",
                                "/search-blogs/**",
                                "/register",
                                "/authenticate",
                                "/add-comment",
                                "/send-mail",
                                "/reset-password",
                                "/actuator/**"
                        ).permitAll()

                        // ✅ ADMIN ENDPOINTS - Require ROLE_ADMIN
                        .pathMatchers(

                                "/get-all-comments",
                                "/comment/**",
                                "/add-blog",
                                "/update-blog/**",
                                "/delete-blog/**"
                        ).hasAuthority("ROLE_ADMIN")

                        // ✅ DEFAULT - All other endpoints require authentication
                        .anyExchange().authenticated()
                )
                .authenticationManager(authenticationProvider())
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
                .addFilterAt(jwtFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutHandler((webFilterExchange, authentication) -> {
                            ServerHttpRequest request = webFilterExchange.getExchange().getRequest();
                            return Mono.justOrEmpty(request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION))
                                    .flatMap(authHeader -> {
                                        if (authHeader.startsWith("Bearer ")) {
                                            String jwt = authHeader.substring(7);
                                            log.info("Before token blacklisting");
                                            tokenBlackListService.add(jwt);
                                            return Mono.empty();
                                        }
                                        log.info("Authorization header is missing or invalid");
                                        return Mono.empty();
                                    });
                        })
                        .logoutSuccessHandler((webFilterExchange, authentication) -> {
                            ServerHttpResponse response = webFilterExchange.getExchange().getResponse();
                            response.setStatusCode(HttpStatus.OK);
                            return response.writeWith(Mono.just(response.bufferFactory()
                                    .wrap("Logout successful".getBytes())));
                        })
                )
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .build();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public ReactiveAuthenticationManager authenticationProvider() {
        UserDetailsRepositoryReactiveAuthenticationManager dao = new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService);
        dao.setPasswordEncoder(passwordEncoder());
        return dao;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("https://codeverse-chronicles.vercel.app"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true); // Important for cookies / Authorization header
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }
}
