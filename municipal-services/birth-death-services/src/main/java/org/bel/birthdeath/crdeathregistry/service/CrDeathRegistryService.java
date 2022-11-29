package org.bel.birthdeath.crdeathregistry.service;

import java.util.List;

import org.bel.birthdeath.crdeath.kafka.producer.CrDeathProducer;
import org.bel.birthdeath.crdeathregistry.config.CrDeathRegistryConfiguration;
import org.bel.birthdeath.crdeathregistry.enrichment.CrDeathRegistryEnrichment;
import org.bel.birthdeath.crdeathregistry.web.models.CrDeathRegistryDtl;
import org.bel.birthdeath.crdeathregistry.web.models.CrDeathRegistryRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
     * Creates CrDeathService
     * Rakhi S IKM
     * on 28.11.2022
     */
 @Service
 
public class CrDeathRegistryService {

    private final CrDeathProducer producer;
    private final CrDeathRegistryConfiguration deathConfig;
    private final CrDeathRegistryEnrichment enrichmentService;


    @Autowired
    CrDeathRegistryService(CrDeathProducer producer,CrDeathRegistryConfiguration deathConfig,
    CrDeathRegistryEnrichment enrichmentService){
        this.producer = producer;
        this.deathConfig = deathConfig;
        this.enrichmentService = enrichmentService;
    }

    public List<CrDeathRegistryDtl> create(CrDeathRegistryRequest request) {
        // validate request
       // validatorService.validateCreate(request);

       // validate mdms data       
       // Object mdmsData = util.mDMSCall(request.getRequestInfo(), request.getDeathCertificateDtls().get(0).getTenantId());
         
         /********************************************* */

        // try {
        //     ObjectMapper mapper = new ObjectMapper();
        //     Object obj = mdmsData;
        //     mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        //    System.out.println("mdmsDataRegistry "+ mapper.writeValueAsString(obj));
        //     }catch(Exception e) {
        //        // log.error("Exception while fetching from searcher: ",e);
        //     }


            /********************************************** */
         //   mdmsValidator.validateMDMSData(request,mdmsData);

         // enrich request
          enrichmentService.enrichCreate(request);
        //IDGen call
      //  enrichmentService.setIdgenIds(request);    

            producer.push(deathConfig.getSaveDeathRegistryTopic(), request);

        return request.getDeathCertificateDtls();
    }
    
}
