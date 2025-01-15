package org.egov.web.notification.sms.models;

import static org.apache.commons.lang3.RandomStringUtils.randomNumeric;
import static org.springframework.util.StringUtils.isEmpty;

import org.egov.web.notification.sms.exception.InvalidTokenRequestException;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class TokenRequest {

    private String identity;
    private String tenantId;

    public void validate() {
        if (isIdentityAbsent() || isTenantIdAbsent()) {
            throw new InvalidTokenRequestException(this);
        }
    }

    public boolean isTenantIdAbsent() {
        return isEmpty(tenantId);
    }

    public boolean isIdentityAbsent() {
        return isEmpty(identity);
    }

    public String generateToken() {
        return randomNumeric(5);
    }
}

