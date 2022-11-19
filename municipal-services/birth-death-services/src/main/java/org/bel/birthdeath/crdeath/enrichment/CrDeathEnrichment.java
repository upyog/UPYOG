package org.bel.birthdeath.crdeath.enrichment;

// import java.text.SimpleDateFormat;
import java.util.UUID;

import org.bel.birthdeath.crdeath.web.models.AuditDetails;
import org.bel.birthdeath.crdeath.web.models.CrDeathDtlRequest;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.User;
import org.springframework.stereotype.Component;

@Component
public class CrDeathEnrichment implements BaseEnrichment{

    public void enrichCreate(CrDeathDtlRequest request) {

        RequestInfo requestInfo = request.getRequestInfo();
        User userInfo = requestInfo.getUserInfo();

        AuditDetails auditDetails = buildAuditDetails(userInfo.getUuid(), Boolean.TRUE);

        request.getDeathCertificateDtls()
               .forEach(deathdtls -> {
                deathdtls.setId(UUID.randomUUID().toString());
                deathdtls.setAuditDetails(auditDetails);
                deathdtls.getStatisticalInfo().setId(UUID.randomUUID().toString());               
             
                // String str = new SimpleDateFormat("dd/MM/yyyy").format(deathdtls.getDateOfDeath() * 1000);
                // System.out.println("DOD Epoc"+str);

                deathdtls.getAddressInfo().get(0).setParentdeathDtlId(deathdtls.getId());
                deathdtls.getAddressInfo().get(0).setAuditDetails(auditDetails);
                deathdtls.getAddressInfo().forEach(addressdtls -> {
                      addressdtls.getPresentAddress().setId(UUID.randomUUID().toString());
                      addressdtls.getPermanentAddress().setId(UUID.randomUUID().toString());
                      addressdtls.getInformantAddress().setId(UUID.randomUUID().toString());
                      addressdtls.getDeathplaceAddress().setId(UUID.randomUUID().toString());
                      addressdtls.getBurialAddress().setId(UUID.randomUUID().toString());                       
                     });
               });
    }
}
