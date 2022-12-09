package org.bel.birthdeath.crdeath.web.controllers;

import java.util.List;

import javax.validation.Valid;

import org.bel.birthdeath.crdeath.service.CrDeathService;

import org.bel.birthdeath.crdeath.web.models.CrDeathDtl;
import org.bel.birthdeath.crdeath.web.models.CrDeathDtlRequest;
import org.bel.birthdeath.crdeath.web.models.CrDeathDtlResponse;
import org.bel.birthdeath.crdeath.web.models.CrDeathSearchCriteria;
import org.bel.birthdeath.crdeath.web.models.RequestInfoWrapper;
import org.bel.birthdeath.utils.ResponseInfoFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import lombok.extern.slf4j.Slf4j;

//import io.swagger.v3.oas.annotations.parameters.RequestBody;
/**
     * Creates CrDeathController 
     * Rakhi S IKM
     * 
     */

@Slf4j
@RestController
@RequestMapping("/v1")
@Validated
public class CrDeathController {

    @Autowired
    private ResponseInfoFactory responseInfoFactory;

	private final CrDeathService deathService;

    
    @Autowired
    public CrDeathController(CrDeathService deathService) {

        this.deathService = deathService;
    }

    @PostMapping("/crdeathdetails/_create")
    public ResponseEntity<CrDeathDtlResponse> create(@Valid @RequestBody CrDeathDtlRequest request) {

        System.out.println("hai");
           /********************************************* */

        try {
                ObjectMapper mapper = new ObjectMapper();
                Object obj = request;
                mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
               System.out.println("rakhi3 "+ mapper.writeValueAsString(obj));
        }catch(Exception e) {
            log.error("Exception while fetching from searcher: ",e);
        }

 
        /********************************************** */
        
        List<CrDeathDtl> deathDetails = deathService.create(request);

        CrDeathDtlResponse response = CrDeathDtlResponse.builder()
                             .responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(request.getRequestInfo(), Boolean.TRUE))                                                            
                                                                      .deathCertificateDtls(deathDetails)
                                                                      .build();
        return ResponseEntity.ok(response);
    }

    //RAkhi S on 05.12.2022
  //  @Override
    @PostMapping("/crdeathdetails/_search")
    public ResponseEntity<CrDeathDtlResponse> search(@RequestBody RequestInfoWrapper request,
                                                            @ModelAttribute CrDeathSearchCriteria criteria) {

     /********************************************* */

        try {
            ObjectMapper mapper = new ObjectMapper();
            Object obj = request;
            mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
           System.out.println("rakhiSearchRequest "+ mapper.writeValueAsString(obj));
         }catch(Exception e) {
        log.error("Exception while fetching from searcher: ",e);
    }


    /********************************************** */
     /********************************************* */

     try {
        ObjectMapper mapper = new ObjectMapper();
        Object obj = criteria;
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
       System.out.println("rakhiSearchcriteria "+ mapper.writeValueAsString(obj));
     }catch(Exception e) {
    log.error("Exception while fetching from searcher: ",e);
}


/********************************************** */

        List<CrDeathDtl> deathDetails = deathService.search(criteria, request.getRequestInfo());

        CrDeathDtlResponse response = CrDeathDtlResponse.builder()
                             .responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(request.getRequestInfo(), Boolean.TRUE))                                                            
                                                                      .deathCertificateDtls(deathDetails)
                                                                      .build();
        return ResponseEntity.ok(response);
    }
}
