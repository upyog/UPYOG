package org.egov.filemgmnt.enrichment;

import java.util.UUID;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.User;
import org.egov.filemgmnt.web.models.AuditDetails;
import org.egov.filemgmnt.web.models.ServiceDetailsRequest;
import org.springframework.stereotype.Component;

@Component
public class ServiceDetailsEnrichment implements BaseEnrichment {

    /**
     * Enrich create.
     *
     * @param request the {@link org.egov.filemgmnt.web.models.ServiceDetailsRequest
     *                ServiceDetailsRequest}
     */
    public void enrichCreate(ServiceDetailsRequest request) {

        RequestInfo requestInfo = request.getRequestInfo();
        User userInfo = requestInfo.getUserInfo();

        AuditDetails auditDetails = buildAuditDetails(userInfo.getUuid(), Boolean.TRUE);

        request.getServiceDetails()
               .forEach(personal -> {
                   personal.setId(UUID.randomUUID()
                                      .toString());
                   personal.setAuditDetails(auditDetails);
               });
    }

    /**
     * Enrich update.
     *
     * @param request the {@link org.egov.filemgmnt.web.models.ServiceDetailsRequest
     *                ServiceDetailsRequest}
     */
    public void enrichUpdate(ServiceDetailsRequest request) {

        RequestInfo requestInfo = request.getRequestInfo();
        User userInfo = requestInfo.getUserInfo();

        AuditDetails auditDetails = buildAuditDetails(userInfo.getUuid(), Boolean.FALSE);

        request.getServiceDetails()
               .forEach(personal -> personal.setAuditDetails(auditDetails));
    }

}
