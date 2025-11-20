package org.egov.user.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.egov.user.domain.model.SecureUser;
import org.egov.user.domain.model.User;
import org.egov.user.domain.model.enums.UserType;
import org.egov.user.domain.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/oauth")
@Slf4j
public class CustomAuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtEncoder jwtEncoder;

    @Autowired
    private UserService userService;

    @Value("${access.token.validity.in.minutes}")
    private int accessTokenValidityInMinutes;

    @Value("${refresh.token.validity.in.minutes}")
    private int refreshTokenValidityInMinutes;

    @Value("${oauth2.token.format:jwt}")
    private String tokenFormat;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @PostMapping("/token")
    public ResponseEntity<?> authenticate(@RequestParam("grant_type") String grantType,
                                        @RequestParam(value = "username", required = false) String username,
                                        @RequestParam(value = "password", required = false) String password,
                                        @RequestParam(value = "tenantId", required = false) String tenantId,
                                        @RequestParam(value = "userType", required = false) String userType,
                                        @RequestParam(value = "scope", required = false) String scope,
                                        @RequestParam(value = "isInternal", required = false) String isInternal,
                                        HttpServletRequest request) {

        log.info("Custom token endpoint called with grant_type: {}", grantType);

        try {
            if ("password".equals(grantType)) {
                return handlePasswordGrant(username, password, tenantId, userType, scope, request);
            } else if ("client_credentials".equals(grantType)) {
                return handleClientCredentialsGrant(scope);
            } else {
                return ResponseEntity.badRequest()
                    .body(createErrorResponse("unsupported_grant_type", "Grant type not supported: " + grantType));
            }
        } catch (BadCredentialsException e) {
            log.error("Authentication failed for user: {}", username, e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(createErrorResponse("invalid_grant", "Invalid username or password"));
        } catch (Exception e) {
            log.error("Token generation failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("server_error", "Token generation failed: " + e.getMessage()));
        }
    }

    private ResponseEntity<?> handlePasswordGrant(String username, String password,
                                                String tenantId, String userType,
                                                String scope, HttpServletRequest request) {

        // CRITICAL FIX: Fallback to HttpServletRequest parameters if @RequestParam didn't capture them
        // This handles cases where parameters might be in different encoding or Content-Type
        // Pattern already proven working in line 114 of this same controller
        if (username == null) {
            username = request.getParameter("username");
            // Support citizen login with mobileNumber parameter (common in UPYOG frontends)
            if (username == null) {
                username = request.getParameter("mobileNumber");
                if (username != null) {
                    log.info("Using mobileNumber parameter as username for login");
                }
            }
        }
        if (password == null) {
            password = request.getParameter("password");
        }
        if (tenantId == null) {
            tenantId = request.getParameter("tenantId");
        }
        if (userType == null) {
            userType = request.getParameter("userType");
        }
        if (scope == null) {
            scope = request.getParameter("scope");
        }

        log.info("Handling password grant for user: {} in tenant: {} with userType: {}", username, tenantId, userType);

        if (username == null || password == null || tenantId == null || userType == null) {
            log.error("Missing required parameters after fallback - username: {}, password: {}, tenantId: {}, userType: {}",
                     username != null, password != null, tenantId, userType);
            return ResponseEntity.badRequest()
                .body(createErrorResponse("invalid_request", "Missing required parameters"));
        }

        try {
            // Create authentication token with details
            UsernamePasswordAuthenticationToken authToken = 
                new UsernamePasswordAuthenticationToken(username, password);
            
            // Set additional details for custom authentication provider
            Map<String, Object> details = new HashMap<>();
            details.put("tenantId", tenantId);
            details.put("userType", userType);
            // Only set isInternal if it was explicitly passed as a parameter
            // Never hardcode it to "true" for regular user login
            if (request.getParameter("isInternal") != null) {
                details.put("isInternal", request.getParameter("isInternal"));
            }
            authToken.setDetails(details);
            
            // Authenticate using existing providers
            Authentication authentication = authenticationManager.authenticate(authToken);
            
            if (authentication.isAuthenticated() && authentication.getPrincipal() instanceof SecureUser) {
                SecureUser secureUser = (SecureUser) authentication.getPrincipal();
                
                // Reset failed login attempts - SecureUser.getUser() returns contract User
                if (secureUser.getUser() != null && secureUser.getUser().getUuid() != null) {
                    try {
                        // SecureUser.getUser() already returns the contract User type
                        userService.resetFailedLoginAttempts(secureUser.getUser());
                    } catch (Exception e) {
                        log.warn("Could not reset failed login attempts for user: {} - {}", username, e.getMessage());
                    }
                }
                
                // Generate tokens
                String accessToken = generateAccessToken(secureUser, scope);
                String refreshToken = generateRefreshToken(secureUser);

                Map<String, Object> response = createSuccessResponse(accessToken, refreshToken, scope, secureUser);
                
                log.info("Successfully generated tokens for user: {}", username);
                return ResponseEntity.ok(response);
            } else {
                throw new BadCredentialsException("Authentication failed - invalid user type");
            }
            
        } catch (AuthenticationException e) {
            log.warn("Authentication failed for user: {} - {}", username, e.getMessage());
            
            // Handle failed login using existing service
            try {
                RequestInfo requestInfo = RequestInfo.builder()
                    .action("authenticate")
                    .ts(System.currentTimeMillis())
                    .build();
                User user = userService.getUniqueUser(username, tenantId, UserType.fromValue(userType), requestInfo);
                if (user != null) {
                    userService.handleFailedLogin(user, getClientIpAddress(request), requestInfo);
                }
            } catch (Exception ex) {
                log.warn("Could not handle failed login for user: {} - {}", username, ex.getMessage());
            }
            
            throw new BadCredentialsException("Invalid username or password");
        } catch (Exception e) {
            log.error("Unexpected error during password grant", e);
            throw new RuntimeException("Authentication failed", e);
        }
    }

    private ResponseEntity<?> handleClientCredentialsGrant(String scope) {
        log.info("Handling client credentials grant");
        
        try {
            String accessToken = generateClientCredentialsToken(scope);
            
            Map<String, Object> response = new HashMap<>();
            response.put("access_token", accessToken);
            response.put("token_type", "Bearer");
            response.put("expires_in", accessTokenValidityInMinutes * 60);
            response.put("scope", scope != null ? scope : "read write");
            
            Map<String, Object> responseInfo = createResponseInfo("Client Access Token generated successfully");
            response.put("ResponseInfo", responseInfo);
            response.put("grant_type", "client_credentials");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error generating client credentials token", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("server_error", "Failed to generate client credentials token"));
        }
    }

    /**
     * Convert domain User to contract User for UserService method compatibility
     */

    private String generateAccessToken(SecureUser secureUser, String scope) {
        if ("opaque".equals(tokenFormat)) {
            return generateOpaqueAccessToken(secureUser, scope);
        } else {
            return generateJwtAccessToken(secureUser, scope);
        }
    }

    private String generateOpaqueAccessToken(SecureUser secureUser, String scope) {
        // Generate UUID-based token
        String tokenValue = java.util.UUID.randomUUID().toString();

        // Store token metadata in Redis
        Map<String, Object> tokenMetadata = new HashMap<>();

        org.egov.user.web.contract.auth.User user = secureUser.getUser();
        if (user != null) {
            // CRITICAL FIX: Decrypt user data BEFORE storing in Redis
            // This ensures that subsequent token lookups (_details endpoint) have decrypted data
            // Without this, CITIZEN users get encrypted mobile numbers in token metadata
            // because ABAC policy cannot validate "Self" purpose with encrypted identifiers in RequestInfo
            org.egov.user.web.contract.auth.User userToStore = user;
            try {
                log.info("REDIS STORE: Attempting to decrypt user data before storing. User ID: {}, Type: {}",
                    user.getId(), user.getType());
                org.egov.user.domain.model.User domainUser = convertContractToDomainUser(user);
                org.egov.user.domain.model.User decryptedDomainUser = userService.decryptUserWithContext(domainUser, user);
                userToStore = convertToContractUser(decryptedDomainUser);

                // Check if mobile number was successfully decrypted
                boolean mobileDecrypted = userToStore.getMobileNumber() != null &&
                    !userToStore.getMobileNumber().contains("|");
                log.info("REDIS STORE: User data decryption completed. Mobile decrypted: {}, UserName decrypted: {}",
                    mobileDecrypted,
                    userToStore.getUserName() != null && !userToStore.getUserName().contains("|"));
            } catch (Exception e) {
                log.warn("REDIS STORE: Failed to decrypt user data, storing encrypted data. Error: {} - {}",
                    e.getClass().getSimpleName(), e.getMessage());
                // Fall back to storing encrypted data (maintains backward compatibility)
                userToStore = user;
            }

            // Log roles before storing
            log.info("REDIS STORE: User {} has {} roles", userToStore.getUserName(),
                     userToStore.getRoles() != null ? userToStore.getRoles().size() : "null");

            // Store minimal user data (now decrypted if decryption succeeded)
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("id", userToStore.getId());
            userInfo.put("uuid", userToStore.getUuid());
            userInfo.put("userName", userToStore.getUserName());
            userInfo.put("name", userToStore.getName());
            userInfo.put("mobileNumber", userToStore.getMobileNumber());
            userInfo.put("emailId", userToStore.getEmailId());
            userInfo.put("locale", userToStore.getLocale());
            userInfo.put("type", userToStore.getType());
            userInfo.put("tenantId", userToStore.getTenantId());
            userInfo.put("active", userToStore.isActive());

            // CRITICAL FIX: Convert roles to serializable format for Redis
            // Redis cannot properly serialize/deserialize Set<Role> objects
            // So we convert to List<Map<String, String>> which preserves all role data
            if (userToStore.getRoles() != null && !userToStore.getRoles().isEmpty()) {
                java.util.List<Map<String, String>> rolesList = userToStore.getRoles().stream()
                    .map(role -> {
                        Map<String, String> roleMap = new HashMap<>();
                        roleMap.put("code", role.getCode());
                        roleMap.put("name", role.getName());
                        roleMap.put("tenantId", role.getTenantId());
                        return roleMap;
                    })
                    .collect(Collectors.toList());
                userInfo.put("roles", rolesList);
                log.info("REDIS STORE: Converted {} roles to Map format for Redis storage", rolesList.size());
                rolesList.forEach(roleMap ->
                    log.info("  REDIS STORE: Storing role - code: {}, name: {}, tenantId: {}",
                        roleMap.get("code"), roleMap.get("name"), roleMap.get("tenantId"))
                );
            } else {
                userInfo.put("roles", new java.util.ArrayList<>());
                log.warn("REDIS STORE: User has null/empty roles, storing empty list");
            }

            tokenMetadata.put("UserRequest", userInfo);
            tokenMetadata.put("userId", userToStore.getId());
            tokenMetadata.put("userName", userToStore.getUserName());
            tokenMetadata.put("userType", userToStore.getType());
            tokenMetadata.put("tenantId", userToStore.getTenantId());
        }
        
        // Add ResponseInfo
        Map<String, Object> responseInfo = createResponseInfo("Access Token generated successfully");
        tokenMetadata.put("ResponseInfo", responseInfo);
        tokenMetadata.put("scope", scope != null ? scope : "read write");
        tokenMetadata.put("token_type", "access_token");

        // Store in Redis with expiration
        String key = "access_token:" + tokenValue;
        redisTemplate.opsForValue().set(key, tokenMetadata, accessTokenValidityInMinutes, java.util.concurrent.TimeUnit.MINUTES);
        
        return tokenValue;
    }

    private String generateJwtAccessToken(SecureUser secureUser, String scope) {
        Instant now = Instant.now();
        Instant expiry = now.plus(accessTokenValidityInMinutes, ChronoUnit.MINUTES);
        
        JwtClaimsSet.Builder claimsBuilder = JwtClaimsSet.builder()
            .issuer("http://localhost:8080")
            .subject(secureUser.getUsername())
            .audience(java.util.List.of("egov-user-client"))
            .issuedAt(now)
            .expiresAt(expiry)
            .claim("scope", scope != null ? scope : "read write")
            .claim("token_type", "access_token");

        // Add user-specific claims - SecureUser.getUser() returns contract User
        org.egov.user.web.contract.auth.User user = secureUser.getUser();
        if (user != null) {
            claimsBuilder
                .claim("userId", user.getId())
                .claim("userName", user.getUserName())
                .claim("userType", user.getType())
                .claim("tenantId", user.getTenantId())
                .claim("UserRequest", user);
        }

        // Add ResponseInfo
        Map<String, Object> responseInfo = createResponseInfo("Access Token generated successfully");
        claimsBuilder.claim("ResponseInfo", responseInfo);

        JwtClaimsSet claims = claimsBuilder.build();
        return this.jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    private String generateRefreshToken(SecureUser secureUser) {
        if ("opaque".equals(tokenFormat)) {
            return generateOpaqueRefreshToken(secureUser);
        } else {
            return generateJwtRefreshToken(secureUser);
        }
    }

    private String generateOpaqueRefreshToken(SecureUser secureUser) {
        // Generate UUID-based refresh token
        String tokenValue = java.util.UUID.randomUUID().toString();
        
        // Store refresh token metadata in Redis
        Map<String, Object> tokenMetadata = new HashMap<>();
        tokenMetadata.put("tokenType", "refresh_token");
        tokenMetadata.put("issuedAt", Instant.now().toEpochMilli());
        
        // Store minimal data for refresh tokens
        if (secureUser != null) {
            tokenMetadata.put("subject", secureUser.getUsername());
            org.egov.user.web.contract.auth.User user = secureUser.getUser();
            if (user != null) {
                tokenMetadata.put("userId", user.getId());
                tokenMetadata.put("tenantId", user.getTenantId());
            }
        }
        
        // Store in Redis with expiration
        String key = "refresh_token:" + tokenValue;
        redisTemplate.opsForValue().set(key, tokenMetadata, refreshTokenValidityInMinutes, java.util.concurrent.TimeUnit.MINUTES);
        
        return tokenValue;
    }

    private String generateJwtRefreshToken(SecureUser secureUser) {
        Instant now = Instant.now();
        Instant expiry = now.plus(refreshTokenValidityInMinutes, ChronoUnit.MINUTES);
        
        org.egov.user.web.contract.auth.User user = secureUser.getUser();
        JwtClaimsSet.Builder claimsBuilder = JwtClaimsSet.builder()
            .issuer("http://localhost:8080")
            .subject(secureUser.getUsername())
            .audience(java.util.List.of("egov-user-client"))
            .issuedAt(now)
            .expiresAt(expiry)
            .claim("token_type", "refresh_token");
        
        if (user != null) {
            claimsBuilder
                .claim("userId", user.getId())
                .claim("tenantId", user.getTenantId());
        }
        
        JwtClaimsSet claims = claimsBuilder.build();
        return this.jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    private String generateClientCredentialsToken(String scope) {
        if ("opaque".equals(tokenFormat)) {
            return generateOpaqueClientCredentialsToken(scope);
        } else {
            return generateJwtClientCredentialsToken(scope);
        }
    }

    private String generateOpaqueClientCredentialsToken(String scope) {
        // Generate UUID-based token for client credentials
        String tokenValue = java.util.UUID.randomUUID().toString();
        
        // Store token metadata in Redis
        Map<String, Object> tokenMetadata = new HashMap<>();
        tokenMetadata.put("scope", scope != null ? scope : "read write");
        tokenMetadata.put("grant_type", "client_credentials");
        tokenMetadata.put("token_type", "access_token");
        tokenMetadata.put("subject", "egov-user-client");
        
        // Add ResponseInfo
        Map<String, Object> responseInfo = createResponseInfo("Client Access Token generated successfully");
        tokenMetadata.put("ResponseInfo", responseInfo);

        // Store in Redis with expiration
        String key = "access_token:" + tokenValue;
        redisTemplate.opsForValue().set(key, tokenMetadata, accessTokenValidityInMinutes, java.util.concurrent.TimeUnit.MINUTES);
        
        return tokenValue;
    }

    private String generateJwtClientCredentialsToken(String scope) {
        Instant now = Instant.now();
        Instant expiry = now.plus(accessTokenValidityInMinutes, ChronoUnit.MINUTES);
        
        JwtClaimsSet claims = JwtClaimsSet.builder()
            .issuer("http://localhost:8080")
            .subject("egov-user-client")
            .audience(java.util.List.of("egov-user-client"))
            .issuedAt(now)
            .expiresAt(expiry)
            .claim("scope", scope != null ? scope : "read write")
            .claim("grant_type", "client_credentials")
            .claim("token_type", "access_token")
            .build();

        return this.jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    private Map<String, Object> createSuccessResponse(String accessToken, String refreshToken,
                                                    String scope, SecureUser secureUser) {
        Map<String, Object> response = new HashMap<>();
        response.put("access_token", accessToken);
        response.put("refresh_token", refreshToken);
        response.put("token_type", "Bearer");
        response.put("expires_in", accessTokenValidityInMinutes * 60);
        response.put("scope", scope != null ? scope : "read write");

        Map<String, Object> responseInfo = createResponseInfo("Access Token generated successfully");
        response.put("ResponseInfo", responseInfo);

        // UPDATED: User data is already decrypted in generateOpaqueAccessToken before storing in Redis
        // So we can use it directly without additional decryption
        // This was previously causing redundant decryption attempts
        org.egov.user.web.contract.auth.User contractUser = secureUser.getUser();
        if (contractUser != null) {
            // Check if data needs decryption (for backward compatibility or if stored before this fix)
            boolean needsDecryption = contractUser.getMobileNumber() != null &&
                contractUser.getMobileNumber().contains("|");

            if (needsDecryption) {
                log.info("Token response: User data appears encrypted, attempting decryption. User: {}",
                    contractUser.getId());
                try {
                    // Convert contract user to domain user for decryption
                    org.egov.user.domain.model.User domainUser = convertContractToDomainUser(contractUser);
                    // Decrypt user with proper authenticated context
                    org.egov.user.domain.model.User decryptedDomainUser = userService.decryptUserWithContext(domainUser, contractUser);
                    // Convert back to contract user for response
                    org.egov.user.web.contract.auth.User decryptedUser = convertToContractUser(decryptedDomainUser);
                    log.info("Token response: User data decrypted successfully");
                    response.put("UserRequest", decryptedUser);
                } catch (Exception e) {
                    log.error("Token response: Failed to decrypt user data: {}", e.getMessage(), e);
                    log.info("Token response: Falling back to as-is user data");
                    response.put("UserRequest", contractUser);
                }
            } else {
                // Data is already decrypted (new flow after this fix)
                log.info("Token response: User data already decrypted, using as-is. User: {}", contractUser.getId());
                response.put("UserRequest", contractUser);
            }
        }

        return response;
    }

    /**
     * Convert contract User to domain User for decryption
     */
    private org.egov.user.domain.model.User convertContractToDomainUser(org.egov.user.web.contract.auth.User contractUser) {
        if (contractUser == null) {
            return null;
        }

        return org.egov.user.domain.model.User.builder()
            .id(contractUser.getId())
            .uuid(contractUser.getUuid())
            .username(contractUser.getUserName())
            .name(contractUser.getName())
            .mobileNumber(contractUser.getMobileNumber())
            .emailId(contractUser.getEmailId())
            .locale(contractUser.getLocale())
            .active(contractUser.isActive())
            .type(contractUser.getType() != null ? UserType.fromValue(contractUser.getType()) : null)
            .tenantId(contractUser.getTenantId())
            .roles(contractUser.getRoles() != null ?
                contractUser.getRoles().stream()
                    .map(role -> org.egov.user.domain.model.Role.builder()
                        .name(role.getName())
                        .code(role.getCode())
                        .tenantId(role.getTenantId())
                        .build())
                    .collect(Collectors.toSet()) : null)
            .build();
    }

    /**
     * Convert domain User to contract User
     */
    private org.egov.user.web.contract.auth.User convertToContractUser(org.egov.user.domain.model.User domainUser) {
        if (domainUser == null) {
            return null;
        }

        return org.egov.user.web.contract.auth.User.builder()
            .id(domainUser.getId())
            .uuid(domainUser.getUuid())
            .userName(domainUser.getUsername())
            .name(domainUser.getName())
            .mobileNumber(domainUser.getMobileNumber())
            .emailId(domainUser.getEmailId())
            .locale(domainUser.getLocale())
            .active(domainUser.getActive())
            .type(domainUser.getType() != null ? domainUser.getType().name() : null)
            .tenantId(domainUser.getTenantId())
            .roles(convertRoles(domainUser.getRoles()))
            .build();
    }

    /**
     * Convert domain Roles to contract Roles
     */
    private Set<org.egov.user.web.contract.auth.Role> convertRoles(Set<org.egov.user.domain.model.Role> domainRoles) {
        if (domainRoles == null) {
            return new HashSet<>();
        }

        return domainRoles.stream()
            .map(domainRole -> new org.egov.user.web.contract.auth.Role(domainRole))
            .collect(Collectors.toSet());
    }

    private Map<String, Object> createResponseInfo(String status) {
        Map<String, Object> responseInfo = new LinkedHashMap<>();
        responseInfo.put("api_id", "");
        responseInfo.put("ver", "1.0");
        responseInfo.put("ts", System.currentTimeMillis());
        responseInfo.put("res_msg_id", "");
        responseInfo.put("msg_id", "");
        responseInfo.put("status", status);
        return responseInfo;
    }

    private Map<String, Object> createErrorResponse(String error, String description) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", error);
        errorResponse.put("error_description", description);
        return errorResponse;
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }

    /**
     * Token introspection endpoint for opaque tokens
     */
    @PostMapping("/introspect")
    public ResponseEntity<?> introspectToken(@RequestParam("token") String token,
                                           @RequestParam(value = "token_type_hint", required = false) String tokenTypeHint) {
        try {
            if ("opaque".equals(tokenFormat)) {
                // Handle opaque token introspection
                Map<String, Object> tokenMetadata;
                
                if ("refresh_token".equals(tokenTypeHint)) {
                    String key = "refresh_token:" + token;
                    tokenMetadata = (Map<String, Object>) redisTemplate.opsForValue().get(key);
                } else {
                    // Default to access token
                    String key = "access_token:" + token;
                    tokenMetadata = (Map<String, Object>) redisTemplate.opsForValue().get(key);
                }
                
                if (tokenMetadata != null) {
                    Map<String, Object> response = new HashMap<>();
                    response.put("active", true);
                    response.putAll(tokenMetadata);
                    return ResponseEntity.ok(response);
                } else {
                    Map<String, Object> response = new HashMap<>();
                    response.put("active", false);
                    return ResponseEntity.ok(response);
                }
            } else {
                // For JWT tokens, return active=false as they are self-contained
                Map<String, Object> response = new HashMap<>();
                response.put("active", false);
                response.put("error", "JWT tokens do not require introspection");
                return ResponseEntity.ok(response);
            }
        } catch (Exception e) {
            log.error("Error during token introspection", e);
            Map<String, Object> response = new HashMap<>();
            response.put("active", false);
            response.put("error", "introspection_failed");
            return ResponseEntity.ok(response);
        }
    }

    /**
     * Token revocation endpoint
     */
    @PostMapping("/revoke")
    public ResponseEntity<?> revokeToken(@RequestParam("token") String token,
                                       @RequestParam(value = "token_type_hint", required = false) String tokenTypeHint) {
        try {
            if ("opaque".equals(tokenFormat)) {
                if ("refresh_token".equals(tokenTypeHint)) {
                    String key = "refresh_token:" + token;
                    redisTemplate.delete(key);
                } else {
                    // Default to access token
                    String key = "access_token:" + token;
                    redisTemplate.delete(key);
                }
            }
            
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error during token revocation", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("server_error", "Failed to revoke token"));
        }
    }
}