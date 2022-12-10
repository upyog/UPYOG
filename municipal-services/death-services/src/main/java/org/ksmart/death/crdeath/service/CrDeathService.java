package org.ksmart.death.crdeath.service;

import java.util.List;

import org.ksmart.death.crdeath.config.CrDeathConfiguration;
import org.ksmart.death.crdeath.enrichment.CrDeathEnrichment;
import org.ksmart.death.crdeath.kafka.producer.CrDeathProducer;
import org.ksmart.death.crdeath.repository.CrDeathRepository;
import org.ksmart.death.crdeath.util.CrDeathMdmsUtil;
import org.ksmart.death.crdeath.validators.CrDeathValidator;
import org.ksmart.death.crdeath.validators.MDMSValidator;
import org.ksmart.death.crdeath.web.models.CrDeathDtl;
import org.ksmart.death.crdeath.web.models.CrDeathDtlRequest;
import org.ksmart.death.crdeath.web.models.CrDeathSearchCriteria;
import org.egov.common.contract.request.RequestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import io.micrometer.core.ipc.http.HttpSender.Request;

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
    private final CrDeathValidator validatorService;
    private final CrDeathRepository repository;

    @Autowired
    CrDeathService(CrDeathProducer producer,CrDeathConfiguration deathConfig,
                CrDeathEnrichment enrichmentService,CrDeathMdmsUtil util,MDMSValidator mdmsValidator,
                CrDeathValidator validatorService,CrDeathRepository repository){
        this.producer = producer;
        this.deathConfig = deathConfig;
        this.enrichmentService = enrichmentService;
        this.util = util;
        this.mdmsValidator = mdmsValidator;
        this.validatorService = validatorService;
        this.repository = repository;
    }
    
    public List<CrDeathDtl> create(CrDeathDtlRequest request) {
      

       // validate mdms data
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
              // validate request
            validatorService.validateCreate(request,mdmsData);

            mdmsValidator.validateMDMSData(request,mdmsData);

         // enrich request
        enrichmentService.enrichCreate(request);
        //IDGen call
        enrichmentService.setIdgenIds(request);    

        producer.push(deathConfig.getSaveDeathDetailsTopic(), request);

        return request.getDeathCertificateDtls();
    }
    //Rakhi S on 05.12.2022
    public List<CrDeathDtl> search(CrDeathSearchCriteria criteria, RequestInfo requestInfo) {
		// validatorService.validateSearch(requestInfo, criteria);
		return repository.getDeathApplication(criteria);
	}
  //UPDATE BEGIN Jasmine
   public List<CrDeathDtl> update(CrDeathDtlRequest request) {
    
      Object mdmsData = util.mDMSCall(request.getRequestInfo(), request.getDeathCertificateDtls().get(0).getTenantId());
         
      String id = request.getDeathCertificateDtls().get(0).getId();

      List<CrDeathDtl> searchResult = repository.getDeathApplication(CrDeathSearchCriteria.builder().id(id).build());

      validatorService.validateUpdate(request, searchResult);

      //mdmsValidator.validateMDMSData(request,mdmsData);

      enrichmentService.enrichUpdate(request);

      producer.push(deathConfig.getUpdateDeathDetailsTopic(), request);

      return request.getDeathCertificateDtls();
    }
}
