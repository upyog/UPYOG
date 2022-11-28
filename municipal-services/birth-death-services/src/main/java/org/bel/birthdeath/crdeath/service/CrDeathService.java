package org.bel.birthdeath.crdeath.service;

import java.util.List;

import org.bel.birthdeath.crdeath.config.CrDeathConfiguration;
import org.bel.birthdeath.crdeath.enrichment.CrDeathEnrichment;
import org.bel.birthdeath.crdeath.kafka.producer.CrDeathProducer;
import org.bel.birthdeath.crdeath.util.CrDeathMdmsUtil;
import org.bel.birthdeath.crdeath.validators.MDMSValidator;
import org.bel.birthdeath.crdeath.web.models.CrDeathDtl;
import org.bel.birthdeath.crdeath.web.models.CrDeathDtlRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
     * Creates CrDeathService
     * Rakhi S IKM
     * 
     */

@Service
public class CrDeathService {

    private final CrDeathProducer producer;
    private final CrDeathConfiguration deathConfig;
    private final CrDeathEnrichment enrichmentService;
    private final CrDeathMdmsUtil util;
    private final MDMSValidator mdmsValidator;

    @Autowired
    CrDeathService(CrDeathProducer producer,CrDeathConfiguration deathConfig,
                CrDeathEnrichment enrichmentService,CrDeathMdmsUtil util,MDMSValidator mdmsValidator){
        this.producer = producer;
        this.deathConfig = deathConfig;
        this.enrichmentService = enrichmentService;
        this.util = util;
        this.mdmsValidator = mdmsValidator;
    }
    
    public List<CrDeathDtl> create(CrDeathDtlRequest request) {
        // validate request
       // validatorService.validateCreate(request);

       // validate mdms data
        //  Object mdmsData = util.mDMSCall(request.getRequestInfo(), request.getDeathCertificateDtls().get(0).getTenantId());
        Object mdmsData = util.mDMSCall(request.getRequestInfo(), request.getDeathCertificateDtls().get(0).getTenantId());
         
         /********************************************* */

        try {
            ObjectMapper mapper = new ObjectMapper();
            Object obj = mdmsData;
            mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
           System.out.println("mdmsData1 "+ mapper.writeValueAsString(obj));
            }catch(Exception e) {
               // log.error("Exception while fetching from searcher: ",e);
            }


            /********************************************** */
            mdmsValidator.validateMDMSData(request,mdmsData);

         // enrich request
        enrichmentService.enrichCreate(request);
        //IDGen call
        enrichmentService.setIdgenIds(request);    

        producer.push(deathConfig.getSaveDeathDetailsTopic(), request);

        return request.getDeathCertificateDtls();
    }

    
}
