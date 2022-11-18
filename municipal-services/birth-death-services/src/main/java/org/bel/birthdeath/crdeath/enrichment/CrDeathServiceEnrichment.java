package org.bel.birthdeath.crdeath.enrichment;

import java.util.UUID;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.User;
import org.bel.birthdeath.crdeath.web.models.AuditDetails;
import org.bel.birthdeath.crdeath.web.models.CrDeathDtlRequest;
import org.springframework.stereotype.Component;

@Component
public class CrDeathServiceEnrichment implements BaseEnrichment {
    public void enrichCreate(CrDeathDtlRequest request) {

        RequestInfo requestInfo = request.getRequestInfo();
        User userInfo = requestInfo.getUserInfo();

        AuditDetails auditDetails = buildAuditDetails(userInfo.getUuid(), Boolean.TRUE);

        request.getDeathCertificateDtls()
               .forEach(deathdtls -> {
                deathdtls.setId(UUID.randomUUID().toString());
                deathdtls.setAuditDetails(auditDetails);
               
               });
    }

    public void enrichUpdate(CrDeathDtlRequest request) {

        RequestInfo requestInfo = request.getRequestInfo();
        User userInfo = requestInfo.getUserInfo();

        AuditDetails auditDetails = buildAuditDetails(userInfo.getUuid(), Boolean.FALSE);

        request.getDeathCertificateDtls()
               .forEach(deathdtls -> deathdtls.setAuditDetails(auditDetails));
    }
}
