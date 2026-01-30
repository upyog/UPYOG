package org.egov.user.security;

import org.egov.user.domain.service.RedisOAuth2AuthorizationService;
import org.egov.user.security.oauth2.custom.CustomTokenEnhancer;
import org.egov.user.security.oauth2.custom.CustomOpaqueTokenGenerator;
import org.egov.user.security.oauth2.custom.CustomOpaqueRefreshTokenGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.security.oauth2.server.authorization.token.DelegatingOAuth2TokenGenerator;
import org.springframework.security.oauth2.server.authorization.token.JwtGenerator;
import org.springframework.security.oauth2.server.authorization.token.OAuth2AccessTokenGenerator;
import org.springframework.security.oauth2.server.authorization.token.OAuth2RefreshTokenGenerator;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;
import java.util.UUID;

import static org.egov.user.config.UserServiceConstants.USER_CLIENT_ID;

@Configuration
@EnableWebSecurity
public class AuthorizationServerConfiguration {

    @Value("${spring.redis.host}")
    private String host;
    
    @Value("${egov.user.host}")
    private String userHost;

    @Value("${access.token.validity.in.minutes}")
    private int accessTokenValidityInMinutes;

    @Value("${refresh.token.validity.in.minutes}")
    private int refreshTokenValidityInMinutes;

    @Value("${oauth2.token.format:jwt}")
    private String tokenFormat;

    @Autowired
    @Qualifier("customAuthenticationManager")
    private AuthenticationManager customAuthenticationManager;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private CustomTokenEnhancer customTokenEnhancer;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Bean
    @Order(2)
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);

        // Only handle standard OAuth2 endpoints, exclude our custom endpoint
        http.securityMatcher(
            org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher("/oauth2/**")
        );

        http.getConfigurer(OAuth2AuthorizationServerConfigurer.class)
            .oidc(Customizer.withDefaults());

        http.exceptionHandling((exceptions) -> exceptions
            .defaultAuthenticationEntryPointFor(
                new LoginUrlAuthenticationEntryPoint("/login"),
                org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher("/oauth2/**")
            )
        );

        return http.build();
    }

    @Bean
    @Order(3)
    public SecurityFilterChain authServerSecurityFilterChain(HttpSecurity http) throws Exception {
        return http
            .authorizeHttpRequests((authorize) -> authorize
                .requestMatchers("/login", "/error").permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(Customizer.withDefaults())
            .build();
    }

    @Bean
    public OAuth2AuthorizationService authorizationService(RedisTemplate<String, Object> redisTemplate) {
        return new RedisOAuth2AuthorizationService(redisTemplate);
    }

    @Bean
    public RegisteredClientRepository registeredClientRepository() {
        final int accessTokenValidityInSeconds = accessTokenValidityInMinutes * 60;
        final int refreshTokenValidityInSeconds = refreshTokenValidityInMinutes * 60;
        
        RegisteredClient userClient = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId(USER_CLIENT_ID)
                .clientSecret(passwordEncoder.encode("egov-user-secret"))
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                // REMOVED PASSWORD GRANT TYPE - NOT SUPPORTED IN SPRING AUTHORIZATION SERVER
                .redirectUri(userHost + "/login/oauth2/code/" + USER_CLIENT_ID)
                .redirectUri(userHost + "/authorized")               
                .scope("read")
                .scope("write")
                .scope("openid")
                .scope("profile")
                .clientSettings(ClientSettings.builder()
                    .requireAuthorizationConsent(false)
                    .requireProofKey(true) // Enable PKCE for security
                    .build())
                .tokenSettings(TokenSettings.builder()
                    .accessTokenTimeToLive(Duration.ofSeconds(accessTokenValidityInSeconds))
                    .refreshTokenTimeToLive(Duration.ofSeconds(refreshTokenValidityInSeconds))
                    .reuseRefreshTokens(true)
                    .build())
                .build();

        return new InMemoryRegisteredClientRepository(userClient);
    }

    @Bean
    public JWKSource<SecurityContext> jwkSource() {
        KeyPair keyPair = generateRsaKey();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        RSAKey rsaKey = new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID(UUID.randomUUID().toString())
                .build();
        JWKSet jwkSet = new JWKSet(rsaKey);
        return new ImmutableJWKSet<>(jwkSet);
    }

    @Bean
    public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
    }

    @Bean
    public JwtEncoder jwtEncoder(JWKSource<SecurityContext> jwkSource) {
        return new NimbusJwtEncoder(jwkSource);
    }

    @Bean
    public OAuth2TokenGenerator<?> tokenGenerator(JwtEncoder jwtEncoder) {
        if ("opaque".equals(tokenFormat)) {
            // Use opaque UUID-based tokens
            CustomOpaqueTokenGenerator opaqueTokenGenerator = new CustomOpaqueTokenGenerator();
            opaqueTokenGenerator.setRedisTemplate(redisTemplate);
            
            CustomOpaqueRefreshTokenGenerator opaqueRefreshTokenGenerator = new CustomOpaqueRefreshTokenGenerator();
            opaqueRefreshTokenGenerator.setRedisTemplate(redisTemplate);
            
            return new DelegatingOAuth2TokenGenerator(
                opaqueTokenGenerator, opaqueRefreshTokenGenerator);
        } else {
            // Use JWT tokens (default behavior)
            JwtGenerator jwtGenerator = new JwtGenerator(jwtEncoder);
            jwtGenerator.setJwtCustomizer(customTokenEnhancer);
            
            OAuth2AccessTokenGenerator accessTokenGenerator = new OAuth2AccessTokenGenerator();
            OAuth2RefreshTokenGenerator refreshTokenGenerator = new OAuth2RefreshTokenGenerator();
            
            return new DelegatingOAuth2TokenGenerator(
                jwtGenerator, accessTokenGenerator, refreshTokenGenerator);
        }
    }

    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder().build();
    }

    private static KeyPair generateRsaKey() {
        KeyPair keyPair;
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            keyPair = keyPairGenerator.generateKeyPair();
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
        return keyPair;
    }
}