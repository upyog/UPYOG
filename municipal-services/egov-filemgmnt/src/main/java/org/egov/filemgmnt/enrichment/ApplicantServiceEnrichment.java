package org.egov.filemgmnt.enrichment;

import java.util.UUID;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.User;
import org.egov.filemgmnt.web.models.ApplicantServiceRequest;
import org.egov.filemgmnt.web.models.AuditDetails;
import org.springframework.stereotype.Component;

@Component
public class ApplicantServiceEnrichment implements BaseEnrichment {

    public void enrichCreate(ApplicantServiceRequest request) {

        RequestInfo requestInfo = request.getRequestInfo();
        User userInfo = requestInfo.getUserInfo();

        AuditDetails auditDetails = buildAuditDetails(userInfo.getUuid(), Boolean.TRUE);

        request.getApplicantServices()
               .forEach(personal -> {
                   personal.setId(UUID.randomUUID()
                                      .toString());
                   personal.setAuditDetails(auditDetails);
               });
    }

//    public void enrichUpdate(ApplicantPersonalRequest request) {
//
//        RequestInfo requestInfo = request.getRequestInfo();
//        User userInfo = requestInfo.getUserInfo();
//
//        AuditDetails auditDetails = buildAuditDetails(userInfo.getUuid(), Boolean.FALSE);
//
//        request.getApplicantPersonals()
//               .forEach(personal -> personal.setAuditDetails(auditDetails));
//    }
}
