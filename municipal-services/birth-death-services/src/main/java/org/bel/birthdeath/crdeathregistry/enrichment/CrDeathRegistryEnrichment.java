package org.bel.birthdeath.crdeathregistry.enrichment;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.bel.birthdeath.common.Idgen.IdResponse;
import org.bel.birthdeath.common.repository.IdGenRepository;
import org.bel.birthdeath.common.repository.ServiceRequestRepository;
import org.bel.birthdeath.crdeathregistry.config.CrDeathRegistryConfiguration;
import org.bel.birthdeath.crdeathregistry.web.models.AuditDetails;
import org.bel.birthdeath.crdeathregistry.web.models.CrDeathRegistryDtl;
import org.bel.birthdeath.crdeathregistry.web.models.CrDeathRegistryRequest;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.User;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
     * Creates CrDeathRegistryEnrichment for UUID ,Audit details and IDGeneration
     * Rakhi S IKM
     * on 28.11.2022
     */

@Component
public class CrDeathRegistryEnrichment implements BaseEnrichment{

   
	@Autowired
	IdGenRepository idGenRepository;

    @Autowired
	ServiceRequestRepository serviceRequestRepository;

    @Autowired
	CrDeathRegistryConfiguration config;


    public void enrichCreate(CrDeathRegistryRequest request) {

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

    // private List<String> getIdList(RequestInfo requestInfo, String tenantId, String idKey,
    //                                String idformat, int count) {
    //     List<IdResponse> idResponses = idGenRepository.getId(requestInfo, tenantId, idKey, idformat, count).getIdResponses();
        
    //     System.out.println("idResponse"+idResponses);
    //     if (CollectionUtils.isEmpty(idResponses))
    //         throw new CustomException("IDGEN ERROR", "No ids returned from idgen Service");

    //     return idResponses.stream()
    //             .map(IdResponse::getId).collect(Collectors.toList());
    // }
    // public void setIdgenIds(CrDeathRegistryRequest request) {
    //     RequestInfo requestInfo = request.getRequestInfo();
    //     // String tenantId = request.getDeathCertificateDtls().get(0).getTenantId();
    //     String tenantId = requestInfo.getUserInfo().getTenantId();
    //     List<CrDeathRegistryDtl> deathDtls = request.getDeathCertificateDtls();
    //     String applNo = getIdList(requestInfo, tenantId, config.getDeathApplnFileCodeName(), config.getDeathApplnFileCodeFormat(), 1).get(0);
    //     deathDtls.get(0).setDeathApplicationNo(applNo);

    //     String ackNo = getIdList(requestInfo, tenantId, config.getDeathAckName(), config.getDeathACKFormat(), 1).get(0);
    //     deathDtls.get(0).setDeathACKNo(ackNo);
    // }    
    
}
