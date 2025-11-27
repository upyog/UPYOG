package org.egov.user.security.oauth2.custom.authproviders;

import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.egov.tracer.model.ServiceCallException;
import org.egov.user.domain.exception.DuplicateUserNameException;
import org.egov.user.domain.exception.UserNotFoundException;
import org.egov.user.domain.model.SecureUser;
import org.egov.user.domain.model.User;
import org.egov.user.domain.model.enums.UserType;
import org.egov.user.domain.service.UserService;
import org.egov.user.domain.service.utils.EncryptionDecryptionUtil;
import org.egov.user.web.contract.auth.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static org.egov.user.config.UserServiceConstants.IP_HEADER_NAME;
import static org.springframework.util.StringUtils.isEmpty;

@Component("customAuthProvider")
@Slf4j
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private UserService userService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EncryptionDecryptionUtil encryptionDecryptionUtil;

    @Value("${citizen.login.password.otp.enabled}")
    private boolean citizenLoginPasswordOtpEnabled;

    @Value("${employee.login.password.otp.enabled}")
    private boolean employeeLoginPasswordOtpEnabled;

    @Value("${citizen.login.password.otp.fixed.value}")
    private String fixedOTPPassword;

    @Value("${citizen.login.password.otp.fixed.enabled}")
    private boolean fixedOTPEnabled;

    @Autowired
    private HttpServletRequest request;

    public CustomAuthenticationProvider(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = (String) authentication.getCredentials();
        
        log.info("=== Authentication Debug Info ===");
        log.info("Username: {}", username);
        log.info("Password length: {}", password != null ? password.length() : "null");
        log.info("Authentication details: {}", authentication.getDetails());
        
        // Extract tenantId and userType from authentication details
        String tenantId = null;
        String userType = null;
        
        try {
            tenantId = getTenantId(authentication);
            userType = getUserType(authentication);
            log.info("Extracted - TenantId: {}, UserType: {}", tenantId, userType);
        } catch (Exception e) {
            log.error("Failed to extract tenantId/userType: {}", e.getMessage());
            throw new BadCredentialsException("Failed to extract authentication details: " + e.getMessage());
        }
        
        log.debug("Authenticating user: {} with tenantId: {} and userType: {}", username, tenantId, userType);
        
        try {
            // Get user from database
            log.info("Looking up user in database...");
            RequestInfo requestInfo = RequestInfo.builder()
                .action("authenticate")
                .ts(System.currentTimeMillis())
                .build();
            User user = userService.getUniqueUser(username, tenantId, UserType.fromValue(userType), requestInfo);
            
            if (user == null) {
                log.error("User not found in database for username: {}, tenantId: {}, userType: {}", username, tenantId, userType);
                throw new BadCredentialsException("User not found");
            }
            
            log.info("User found - ID: {}, UUID: {}, Active: {}", user.getId(), user.getUuid(), user.getActive());

            // Check account status
            if (user.getAccountLocked() != null && user.getAccountLocked()) {
                log.warn("Account is locked for user: {}", username);
                if (!userService.isAccountUnlockAble(user)) {
                    throw new BadCredentialsException("Account locked");
                }
                // If account is unlockable, unlock it
                log.info("Account is unlockable, attempting to unlock...");
                RequestInfo unlockRequestInfo = RequestInfo.builder()
                    .action("unlock")
                    .ts(System.currentTimeMillis())
                    .build();
                user = unlockAccount(user, unlockRequestInfo);
            }

            // Validate password
            UserType userTypeEnum = UserType.fromValue(userType);
            boolean isOtpBased = (userTypeEnum == UserType.CITIZEN && citizenLoginPasswordOtpEnabled) ||
                               (userTypeEnum == UserType.EMPLOYEE && employeeLoginPasswordOtpEnabled);

            log.info("Password validation - OTP based: {}, UserType: {}", isOtpBased, userTypeEnum);

            // CRITICAL FIX: Restore fixed OTP validation logic from Dev-3.0 (lost during upgrade)
            // This allows citizen login with fixed OTP for testing/automation without calling external OTP service
            boolean isPasswordMatched;
            if (userTypeEnum == UserType.CITIZEN && isOtpBased) {
                if (fixedOTPEnabled && fixedOTPPassword != null && !fixedOTPPassword.isEmpty() && fixedOTPPassword.equals(password)) {
                    log.info("Fixed OTP validation successful for citizen user: {}", username);
                    isPasswordMatched = true;  // Skip external OTP service call
                } else {
                    log.info("Fixed OTP not enabled or not matching, validating via OTP service");
                    isPasswordMatched = isPasswordMatch(isOtpBased, password, user, authentication);
                }
            } else {
                isPasswordMatched = isPasswordMatch(isOtpBased, password, user, authentication);
            }

            if (!isPasswordMatched) {
                log.error("Password validation failed for user: {}", username);
                throw new BadCredentialsException("Invalid password");
            }
            
            log.info("Password validation successful");

            // CRITICAL FIX: Decrypt user data during authentication (like Dev-3.0)
            // This ensures both CITIZEN and EMPLOYEE data is decrypted before storing in token
            log.info("Decrypting user data after authentication with UserSelf key");
            try {
                Set<org.egov.user.domain.model.Role> domainRoles = user.getRoles();
                List<org.egov.common.contract.request.Role> contractRoles = new ArrayList<>();
                if (domainRoles != null) {
                    for (org.egov.user.domain.model.Role role : domainRoles) {
                        contractRoles.add(org.egov.common.contract.request.Role.builder()
                            .code(role.getCode())
                            .name(role.getName())
                            .tenantId(role.getTenantId())
                            .build());
                    }
                }

                org.egov.common.contract.request.User userInfo =
                    org.egov.common.contract.request.User.builder()
                        .uuid(user.getUuid())
                        .id(user.getId())
                        .type(user.getType() != null ? user.getType().name() : null)
                        .tenantId(user.getTenantId())
                        .roles(contractRoles)
                        .build();

                RequestInfo decryptRequestInfo = RequestInfo.builder()
                    .userInfo(userInfo)
                    .action("authenticate")
                    .ts(System.currentTimeMillis())
                    .build();

                // IMPORTANT: decryptObject expects a List, not a single object
                // Wrap user in a list, decrypt, then extract back
                List<User> userList = new ArrayList<>();
                userList.add(user);

                List<User> decryptedUserList = encryptionDecryptionUtil.decryptObject(
                    userList,
                    "UserSelf",
                    User.class,
                    decryptRequestInfo
                );

                if (decryptedUserList != null && !decryptedUserList.isEmpty()) {
                    user = decryptedUserList.get(0);
                    log.info("User data decrypted successfully after authentication");
                }
            } catch (Exception e) {
                log.warn("Failed to decrypt user data after authentication: {}. Continuing with encrypted data.", e.getMessage());
                // Continue with encrypted data - will be attempted again during token generation
            }

            // Create SecureUser
            log.info("Creating SecureUser...");
            org.egov.user.web.contract.auth.User authUser = getUser(user);
            log.info("Contract user created with roles: {}", authUser.getRoles());
            
            SecureUser secureUser = new SecureUser(authUser);
            log.info("SecureUser created successfully with authorities: {}", secureUser.getAuthorities());
            
            log.info("Authentication successful for user: {}", username);
            return new UsernamePasswordAuthenticationToken(secureUser, password, secureUser.getAuthorities());
            
        } catch (UserNotFoundException e) {
            log.error("UserNotFoundException for username: {}, tenantId: {}, userType: {} - {}", username, tenantId, userType, e.getMessage());
            throw new BadCredentialsException("User not found: " + e.getMessage());
        } catch (DuplicateUserNameException e) {
            log.error("DuplicateUserNameException for username: {}, tenantId: {}, userType: {} - {}", username, tenantId, userType, e.getMessage());
            throw new BadCredentialsException("Duplicate user found: " + e.getMessage());
        } catch (BadCredentialsException e) {
            log.error("BadCredentialsException: {}", e.getMessage());
            throw e; // Re-throw as-is
        } catch (Exception e) {
            log.error("Unexpected authentication error for user: {} - {}", username, e.getMessage(), e);
            throw new BadCredentialsException("Authentication failed: " + e.getMessage());
        }
    }
    
    private boolean isPasswordMatch(Boolean isOtpBased, String password, User user, Authentication authentication) {
        BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();
        final Map<String, String> details = (Map<String, String>) authentication.getDetails();
        String isCallInternal = details != null ? details.get("isInternal") : null;
        
        // Log the validation attempt
        log.info("Password validation - OTP based: {}, Internal call: {}, User: {}", 
                isOtpBased, isCallInternal, user.getUsername());
        
        if (isOtpBased) {
            // For OTP-based authentication, only skip validation for legitimate internal system calls
            // This should be used very carefully and only for system-to-system communication
            if (null != isCallInternal && isCallInternal.equals("true")) {
                log.warn("Skipping OTP validation for internal system call - User: {}", user.getUsername());
                return true;
            }
            user.setOtpReference(password);
            try {
                boolean otpValid = userService.validateOtp(user);
                log.info("OTP validation result for user {}: {}", user.getUsername(), otpValid);
                return otpValid;
            } catch (ServiceCallException e) {
                log.error("OTP validation failed for user {}: {}", user.getUsername(), e.getMessage());
                return false;
            }
        } else {
            // For password-based authentication, never skip validation for user login
            // The isCallInternal flag should only be used for very specific system operations
            // and should never bypass regular user authentication
            if (null != isCallInternal && isCallInternal.equals("true")) {
                log.warn("Internal call detected but still validating password for security - User: {}", user.getUsername());
                // Continue with normal password validation instead of bypassing
            }
            
            boolean passwordValid = passwordEncoder.matches(password, user.getPassword());
            log.info("Password validation result for user {}: {}", user.getUsername(), passwordValid);
            return passwordValid;
        }
    }

    @SuppressWarnings("unchecked")
    private String getTenantId(Authentication authentication) {
        final Object details = authentication.getDetails();
        
        if (details == null) {
            throw new BadCredentialsException("Authentication details are missing");
        }
        
        Map<String, String> detailsMap;
        
        // Handle both HashMap and LinkedHashMap
        if (details instanceof Map) {
            detailsMap = (Map<String, String>) details;
        } else {
            throw new BadCredentialsException("Authentication details are not in expected format");
        }

        log.debug("Authentication details: {}", detailsMap);
        log.debug("TenantId in CustomAuthenticationProvider: {}", detailsMap.get("tenantId"));

        final String tenantId = detailsMap.get("tenantId");
        if (isEmpty(tenantId)) {
            throw new BadCredentialsException("TenantId is mandatory");
        }
        return tenantId;
    }

    @SuppressWarnings("unchecked")
    private String getUserType(Authentication authentication) {
        final Object details = authentication.getDetails();
        
        Map<String, String> detailsMap;
        
        // Handle both HashMap and LinkedHashMap
        if (details instanceof Map) {
            detailsMap = (Map<String, String>) details;
        } else {
            // Default to CITIZEN if details are not available
            return "CITIZEN";
        }
        
        String userType = detailsMap.get("userType");
        
        // Default to CITIZEN if not provided
        if (isEmpty(userType)) {
            userType = "CITIZEN";
        }
        return userType;
    }

    private org.egov.user.web.contract.auth.User getUser(User user) {
        // CRITICAL: Log role information from domain user
        log.info("ROLE FLOW: Domain user has {} roles",
                 user.getRoles() != null ? user.getRoles().size() : "null");
        if (user.getRoles() != null) {
            user.getRoles().forEach(role ->
                log.info("ROLE FLOW: Domain role - code: {}, name: {}, tenantId: {}",
                         role.getCode(), role.getName(), role.getTenantId())
            );
        }

        // Convert domain roles to auth roles
        Set<Role> authRoles = toAuthRole(user.getRoles());
        log.info("ROLE FLOW: Converted to {} auth roles", authRoles.size());
        authRoles.forEach(role ->
            log.info("ROLE FLOW: Auth role - code: {}, name: {}, tenantId: {}",
                     role.getCode(), role.getName(), role.getTenantId())
        );

        // Use the Lombok builder pattern
        org.egov.user.web.contract.auth.User.UserBuilder builder = org.egov.user.web.contract.auth.User.builder()
                .id(user.getId())
                .uuid(user.getUuid())
                .userName(user.getUsername())
                .name(user.getName())
                .mobileNumber(user.getMobileNumber())
                .emailId(user.getEmailId())
                .locale(user.getLocale())
                .active(user.getActive() != null ? user.getActive() : false)
                .type(user.getType() != null ? user.getType().name() : null)
                .roles(authRoles)
                .tenantId(user.getTenantId());

        // Add permanent city if address exists
        if (user.getPermanentAddress() != null && user.getPermanentAddress().getCity() != null) {
            builder.permanentCity(user.getPermanentAddress().getCity());
        }

        org.egov.user.web.contract.auth.User authUser = builder.build();
        log.info("ROLE FLOW: Built auth user with {} roles",
                 authUser.getRoles() != null ? authUser.getRoles().size() : "null");

        return authUser;
    }
    
    /**
     * Remove the setUserProperties method since we're using builder pattern
     */

    private Set<Role> toAuthRole(Set<org.egov.user.domain.model.Role> domainRoles) {
        if (domainRoles == null)
            return new HashSet<>();
        return domainRoles.stream().map(org.egov.user.web.contract.auth.Role::new).collect(Collectors.toSet());
    }

    @Override
    public boolean supports(final Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }

    /**
     * Unlock account and disable existing failed login attempts for the user
     *
     * @param user to be unlocked
     * @return Updated user
     */
    private User unlockAccount(User user, RequestInfo requestInfo) {
        User userToBeUpdated = user.toBuilder()
                .accountLocked(false)
                .password(null)
                .build();

        User updatedUser = userService.updateWithoutOtpValidation(userToBeUpdated, requestInfo);
        
        // Use the contract user for resetFailedLoginAttempts
        org.egov.user.web.contract.auth.User contractUser = getUser(updatedUser);
        userService.resetFailedLoginAttempts(contractUser);

        return updatedUser;
    }
}