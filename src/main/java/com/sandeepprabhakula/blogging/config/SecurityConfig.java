package com.sandeepprabhakula.blogging.config;

import com.sandeepprabhakula.blogging.filter.JwtFilter;
import com.sandeepprabhakula.blogging.service.TokenBlackListService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.client.RestTemplate;

@EnableWebSecurity
@Configuration
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtFilter jwtFilter;
    private final TokenBlackListService tokenBlackListService;
    private final Logger log = LoggerFactory.getLogger(SecurityConfig.class);
    @Bean
    public UserDetailsService userDetailsService() {
        return new UserInfoUserDetailsService();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf((csrf) -> csrf.disable())
                .authorizeHttpRequests((authorize) ->
                        authorize.requestMatchers("/get-all-blogs", "/blog/**", "/search-blogs/**", "/register", "/authenticate", "/add-comment", "/send-mail/**", "/reset-password").permitAll()
                ).authorizeHttpRequests((authorize) -> {
                    authorize.requestMatchers("/get-all-comments/**", "/comment/**", "/add-blog", "/update-blog/**", "/delete-blog/**").authenticated();

                })
                .sessionManagement((sm) ->
                        sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessHandler((request, response, authentication) -> {

                            response.setStatus(HttpStatus.OK.value());
                            response.getWriter().write("Logout successful");
                        })
                        .addLogoutHandler((request, response, authentication) -> {
                            // Add any cleanup logic here (e.g., token invalidation)
                            String authHeader = request.getHeader("Authorization");
                            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                                String jwt = authHeader.substring(7);
                                // Add token to blacklist or perform other cleanup
                                log.info("before token blacklisting");
                                tokenBlackListService.add(jwt);
                                log.info("after token blacklisting.");
                            }else{
                                log.info("authHeader is null");
                            }
                        }));
        httpSecurity.cors(Customizer.withDefaults());
        return httpSecurity.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider dao = new DaoAuthenticationProvider();
        dao.setUserDetailsService(userDetailsService());
        dao.setPasswordEncoder(passwordEncoder());
        return dao;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }
}
