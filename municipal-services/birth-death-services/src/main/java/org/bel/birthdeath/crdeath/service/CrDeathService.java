package org.bel.birthdeath.crdeath.service;

import java.util.List;

import org.bel.birthdeath.crdeath.config.CrDeathConfiguration;
import org.bel.birthdeath.crdeath.enrichment.CrDeathEnrichment;
import org.bel.birthdeath.crdeath.kafka.producer.CrDeathProducer;
import org.bel.birthdeath.crdeath.util.CrDeathUtil;
import org.bel.birthdeath.crdeath.web.models.CrDeathDtl;
import org.bel.birthdeath.crdeath.web.models.CrDeathDtlRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

@Service
public class CrDeathService {

    private final CrDeathProducer producer;
    private final CrDeathConfiguration deathConfig;
    private final CrDeathEnrichment enrichmentService;
    private final CrDeathUtil util;
    //private  CrDeathDtlRequest crDeathDtlRequest;

    @Autowired
    CrDeathService(CrDeathProducer producer,CrDeathConfiguration deathConfig,
                CrDeathEnrichment enrichmentService,CrDeathUtil util){//,CrDeathDtlRequest crDeathDtlRequest){
        this.producer = producer;
        this.deathConfig = deathConfig;
        this.enrichmentService = enrichmentService;
        this.util = util;
        //this.crDeathDtlRequest=crDeathDtlRequest;
    }
    
    public List<CrDeathDtl> create(CrDeathDtlRequest request) {
        // validate request
       // validatorService.validateCreate(request);

       //mdms request
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

         // enrich request
        enrichmentService.enrichCreate(request);

        producer.push(deathConfig.getSaveDeathDetailsTopic(), request);

        return request.getDeathCertificateDtls();
    }

    
}
