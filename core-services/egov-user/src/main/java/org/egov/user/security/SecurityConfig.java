package org.egov.user.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired(required = false)
    private CorsConfigurationSource corsConfigurationSource;

//	@Autowired
//	@Qualifier("customAuthProvider")
//	private AuthenticationProvider customAuthProvider;
//
//	@Autowired
//	@Qualifier("preAuthProvider")
//	private AuthenticationProvider preAuthProvider;
//
//	@Override
//	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//
//		auth.authenticationProvider(customAuthProvider).authenticationProvider(preAuthProvider);
//	}

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            // Disable CSRF for API endpoints (required for stateless JWT authentication)
            // Note: CSRF protection via SameSite cookie attribute
            .csrf().disable()

            // Enable CORS with configured sources
            .cors()
            .and()

            // Configure headers for additional security
            .headers()
                .frameOptions().deny() // Prevent clickjacking
                .xssProtection().block(true); // XSS protection
    }
}