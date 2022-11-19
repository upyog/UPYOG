package org.bel.birthdeath.crdeath.web.controllers;

import java.util.List;

import javax.validation.Valid;

import org.bel.birthdeath.crdeath.service.CrDeathService;

import org.bel.birthdeath.crdeath.web.models.CrDeathDtl;
import org.bel.birthdeath.crdeath.web.models.CrDeathDtlRequest;
import org.bel.birthdeath.crdeath.web.models.CrDeathDtlResponse;
import org.bel.birthdeath.utils.ResponseInfoFactory;
// import org.bel.birthdeath.crdeath.web.models.CrDeathRequest;
// import org.bel.birthdeath.crdeath.web.models.CrDeathResponse;
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

//import io.swagger.v3.oas.annotations.parameters.RequestBody;
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
}
