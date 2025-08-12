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
    @Order(3)
    public SecurityFilterChain appSecurityFilterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/actuator/**", "/health/**").permitAll()
                .requestMatchers("/oauth2/**", "/login", "/error").permitAll()
                .requestMatchers("/auth/**").permitAll()  // Allow access to custom auth endpoints
                .anyRequest().authenticated()
            )
            .build();
    }

    // Optional: Separate configuration for API endpoints
    @Bean
    @Order(4)
    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
        return http
            .securityMatcher("/user/**", "/api/**")
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authz -> authz
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2.jwt())
            .build();
    }
}