package org.egov.user.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
//@ConfigurationProperties(prefix = "security.password-change")
@Getter
@Setter
public class PasswordChangeProperties {
    private List<String> allowedRoles;
}
