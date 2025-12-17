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

    // REMOVED: Duplicate bean - CustomAuthenticationManager class already provides this bean
    // @Bean("customAuthenticationManager")
    // public AuthenticationManager customAuthenticationManager() {
    //     return new ProviderManager(Arrays.asList(customAuthProvider, preAuthProvider));
    // }

    @Bean
    @Order(1)
    public SecurityFilterChain oauthSecurityFilterChain(HttpSecurity http) throws Exception {
        return http
            .securityMatcher("/oauth/**")  // OAuth endpoints
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authz -> authz
                .anyRequest().permitAll()
            )
            .build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain appSecurityFilterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/actuator/**", "/health/**").permitAll()
                .requestMatchers("/oauth2/**", "/login", "/error").permitAll()
                .requestMatchers("/user/oauth/**").permitAll()  // Allow access to custom auth endpoints
                .requestMatchers("/_oauth2/**").permitAll()  // Allow access to custom OAuth2 endpoints
                // User Controller endpoints
                .requestMatchers("/_details", "/_search", "/v1/_search",
                               "/citizen/_create", "/users/_createnovalidate",
                               "/users/_updatenovalidate", "/profile/_update",
                               "/digilocker/oauth/token",
                               "/users/v2/_create", "/users/v2/_search", "/users/v2/_update",
                               "/_createAddress", "/_getAddress", "/_updateAddress").permitAll()
                // Password Controller endpoints
                .requestMatchers("/password/_update", "/password/nologin/_update").permitAll()
                // Logout Controller endpoints
                .requestMatchers("/_logout").permitAll()
                .anyRequest().authenticated()
            )
            .build();
    }

    // API endpoints that require authentication
    @Bean
    @Order(5)
    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
        return http
            .securityMatcher(request ->
                // Only match endpoints that explicitly need authentication
                request.getRequestURI().startsWith("/api/") ||
                request.getRequestURI().startsWith("/secure/")
            )
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authz -> authz
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2.opaqueToken(opaque -> {
                // Configure opaque token introspection if needed
                opaque.introspectionUri("http://localhost:8080/user/oauth/introspect");
                opaque.introspectionClientCredentials("egov-user-client", "egov-user-secret");
            }))
            .build();
    }
}