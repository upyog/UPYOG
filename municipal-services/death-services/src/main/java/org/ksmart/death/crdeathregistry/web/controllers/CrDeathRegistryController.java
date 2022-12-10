package org.ksmart.death.crdeathregistry.web.controllers;

import java.util.List;

import javax.validation.Valid;

import org.ksmart.death.crdeathregistry.service.CrDeathRegistryService;
import org.ksmart.death.utils.ResponseInfoFactory;

import org.ksmart.death.crdeathregistry.web.models.CrDeathRegistryDtl;
import org.ksmart.death.crdeathregistry.web.models.CrDeathRegistryRequest;
import org.ksmart.death.crdeathregistry.web.models.CrDeathRegistryResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import lombok.extern.slf4j.Slf4j;

/**
     * Creates CrDeathController 
     * Rakhi S IKM
     * on 28.11.2022
     */

    @Slf4j
    @RestController
    @RequestMapping("/v1")
    @Validated
public class CrDeathRegistryController {

     @Autowired
    private ResponseInfoFactory responseInfoFactory;

	private final CrDeathRegistryService deathService;

    
    @Autowired
    public CrDeathRegistryController(CrDeathRegistryService deathService) {

        this.deathService = deathService;
    }
    @PostMapping("/crdeathregistry/_create")
    public ResponseEntity<CrDeathRegistryResponse> create(@Valid @RequestBody CrDeathRegistryRequest request) {

        System.out.println("hai");
           /********************************************* */

        try {
                ObjectMapper mapper = new ObjectMapper();
                Object obj = request;
                mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
               System.out.println("rakhiRegistry "+ mapper.writeValueAsString(obj));
        }catch(Exception e) {
            log.error("Exception while fetching from searcher: ",e);
        }

 
        /********************************************** */
        
        List<CrDeathRegistryDtl> deathDetails = deathService.create(request);

        CrDeathRegistryResponse response = CrDeathRegistryResponse.builder()
                             .responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(request.getRequestInfo(), Boolean.TRUE))                                                            
                                                                      .deathCertificateDtls(deathDetails)
                                                                      .build();
        return ResponseEntity.ok(response);
    }
}
