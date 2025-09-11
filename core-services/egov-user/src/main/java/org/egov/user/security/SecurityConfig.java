package org.egov.user.security;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Autowired
    @Qualifier("customAuthProvider")
    private AuthenticationProvider customAuthProvider;

    @Autowired
    @Qualifier("preAuthProvider")
    private AuthenticationProvider preAuthProvider;

    @Bean("customAuthenticationManager")
    public AuthenticationManager customAuthenticationManager() {
        return new ProviderManager(Arrays.asList(customAuthProvider, preAuthProvider));
    }

    @Bean
    @Order(1)
    public SecurityFilterChain oauthSecurityFilterChain(HttpSecurity http) throws Exception {
        return http
            .securityMatcher("/user/oauth/**")
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authz -> authz
                .anyRequest().permitAll()
            )
            .build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain userEndpointsSecurityFilterChain(HttpSecurity http) throws Exception {
        return http
            .securityMatcher(request -> 
                request.getRequestURI().equals("/user/_details") ||
                request.getRequestURI().startsWith("/user/_search") ||
                request.getRequestURI().startsWith("/user/v1/_search") ||
                request.getRequestURI().startsWith("/user/citizen/_create") ||
                request.getRequestURI().startsWith("/user/users/_createnovalidate") ||
                request.getRequestURI().startsWith("/user/users/_updatenovalidate") ||
                request.getRequestURI().startsWith("/user/profile/_update") ||
                request.getRequestURI().startsWith("/user/digilocker/oauth/token") ||
                request.getRequestURI().startsWith("/user/password/") ||  // Password endpoints
                request.getRequestURI().equals("/user/_logout")  // Logout endpoint
            )
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authz -> authz
                .anyRequest().permitAll()
            )
            .build();
    }

    @Bean
    @Order(4)
    public SecurityFilterChain appSecurityFilterChain(HttpSecurity http) throws Exception {
        return http
            .securityMatcher(request -> 
                !request.getRequestURI().startsWith("/user/oauth/") &&  // Already handled by Order(1)
                !request.getRequestURI().equals("/user/_details") &&    // Already handled by Order(2)
                !request.getRequestURI().startsWith("/user/_search") &&
                !request.getRequestURI().startsWith("/user/v1/_search") &&
                !request.getRequestURI().startsWith("/user/citizen/_create") &&
                !request.getRequestURI().startsWith("/user/users/_createnovalidate") &&
                !request.getRequestURI().startsWith("/user/users/_updatenovalidate") &&
                !request.getRequestURI().startsWith("/user/profile/_update") &&
                !request.getRequestURI().startsWith("/user/digilocker/oauth/token") &&
                !request.getRequestURI().startsWith("/user/password/") &&
                !request.getRequestURI().equals("/user/_logout") &&
                !request.getRequestURI().startsWith("/login") &&         // Already handled by Order(3)
                !request.getRequestURI().startsWith("/error") &&
                !request.getRequestURI().startsWith("/oauth2/") &&
                !request.getRequestURI().startsWith("/auth/") &&
                !request.getRequestURI().startsWith("/api/")             // Will be handled by Order(5)
            )
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/actuator/**", "/health/**").permitAll()
                .anyRequest().authenticated()
            )
            .build();
    }

    // Optional: Separate configuration for other API endpoints that need JWT
    @Bean
    @Order(5)
    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
        return http
            .securityMatcher(request -> 
                request.getRequestURI().startsWith("/api/")
            )
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authz -> authz
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2.jwt())
            .build();
    }
}