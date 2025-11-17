package org.egov.user.security.oauth2.custom.authproviders;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.egov.user.domain.exception.DuplicateUserNameException;
import org.egov.user.domain.exception.UserNotFoundException;
import org.egov.user.domain.model.SecureUser;
import org.egov.user.domain.model.User;
import org.egov.user.domain.model.enums.UserType;
import org.egov.user.domain.service.UserService;
import org.egov.user.domain.service.utils.EncryptionDecryptionUtil;
import org.egov.user.web.contract.auth.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;

// REMOVED: import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static org.springframework.util.StringUtils.isEmpty;

@Component("preAuthProvider")
@Slf4j
public class CustomPreAuthenticatedProvider implements AuthenticationProvider {

    @Autowired
    private UserService userService;

    @Autowired
    private EncryptionDecryptionUtil encryptionDecryptionUtil;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) authentication.getPrincipal();

        SecureUser secureUser = (SecureUser) token.getPrincipal();
        String userName = secureUser.getUsername();

        final LinkedHashMap<String, String> details = (LinkedHashMap<String, String>) token.getDetails();

        String tenantId = details.get("tenantId");
        String userType = details.get("userType");

        if (isEmpty(tenantId)) {
            // CHANGED: OAuth2Exception -> BadCredentialsException
            throw new BadCredentialsException("TenantId is mandatory");
        }
        if (isEmpty(userType) || isNull(UserType.fromValue(userType))) {
            // CHANGED: OAuth2Exception -> BadCredentialsException
            throw new BadCredentialsException("User Type is mandatory and has to be a valid type");
        }

        User user;
        try {
            RequestInfo requestInfo = RequestInfo.builder().build();
            user = userService.getUniqueUser(userName, tenantId, UserType.fromValue(userType), requestInfo);
        } catch (UserNotFoundException e) {
            log.error("User not found", e);
            // CHANGED: OAuth2Exception -> BadCredentialsException
            throw new BadCredentialsException("Invalid login credentials");
        } catch (DuplicateUserNameException e) {
            log.error("Fatal error, user conflict, more than one user found", e);
            // CHANGED: OAuth2Exception -> BadCredentialsException
            throw new BadCredentialsException("Invalid login credentials");
        }

        if (user.getAccountLocked() == null || user.getAccountLocked()) {
            // CHANGED: OAuth2Exception -> BadCredentialsException
            throw new BadCredentialsException("Account locked");
        }

        List<GrantedAuthority> grantedAuths = new ArrayList<>();
        grantedAuths.add(new SimpleGrantedAuthority("ROLE_" + user.getType()));
        final SecureUser finalUser = new SecureUser(getUser(user));
        return new PreAuthenticatedAuthenticationToken(finalUser, null, grantedAuths);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return PreAuthenticatedAuthenticationToken.class.isAssignableFrom(authentication);
    }

    private org.egov.user.web.contract.auth.User getUser(User user) {
        org.egov.user.web.contract.auth.User authUser = org.egov.user.web.contract.auth.User.builder()
                .id(user.getId()).userName(user.getUsername()).uuid(user.getUuid())
                .name(user.getName()).mobileNumber(user.getMobileNumber()).emailId(user.getEmailId())
                .locale(user.getLocale()).active(user.getActive()).type(user.getType().name())
                .roles(toAuthRole(user.getRoles())).tenantId(user.getTenantId())
                .build();

        if (user.getPermanentAddress() != null)
            authUser.setPermanentCity(user.getPermanentAddress().getCity());

        return authUser;
    }

    private Set<Role> toAuthRole(Set<org.egov.user.domain.model.Role> domainRoles) {
        if (domainRoles == null)
            return new HashSet<>();
        return domainRoles.stream().map(org.egov.user.web.contract.auth.Role::new).collect(Collectors.toSet());
    }
}