package org.egov.user.domain.service.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
public class Constants {

    public static final String SECRET_KEY = "fabd54de82e4456b4b9294e6ecb2ea9812ebe15867ecbfa9787f81284fc74a65";

    public static final String SERVICE_ID = "10000078";

    public static final String CITIZEN_ROLE = "CITIZEN";

    public static final String CITIZEN_PASSWORD = "P@ssw0rd";
    
    @Value(("${state.level.tenant.id}"))
    private String stateLevelTenantId;

    @Value(("${egov.sso.hp.host}"))
    private String ssoHpHost;

    @Value(("${egov.sso.hp.validate.token.endpoint}"))
    private String ssoHpEndpoint;

}
