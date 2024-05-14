
package org.egov.pt.web.controllers;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.egov.common.contract.response.ResponseInfo;
import org.egov.pt.config.PropertyConfiguration;
import org.egov.pt.models.Appeal;
import org.egov.pt.models.AppealCriteria;
import org.egov.pt.models.Property;
import org.egov.pt.models.PropertyCriteria;
import org.egov.pt.models.oldProperty.OldPropertyCriteria;
import org.egov.pt.service.AppealService;
import org.egov.pt.service.FuzzySearchService;
import org.egov.pt.service.MigrationService;
import org.egov.pt.service.PropertyEncryptionService;
import org.egov.pt.service.PropertyService;
import org.egov.pt.util.ResponseInfoFactory;
import org.egov.pt.validator.PropertyValidator;
import org.egov.pt.web.contracts.AppealRequest;
import org.egov.pt.web.contracts.AppealResponse;
import org.egov.pt.web.contracts.PropertyRequest;
import org.egov.pt.web.contracts.PropertyResponse;
import org.egov.pt.web.contracts.RequestInfoWrapper;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/appeal")
public class AppealController {

    @Autowired
    private PropertyService propertyService;
    
    @Autowired
    private AppealService appealService;

    @Autowired
    private ResponseInfoFactory responseInfoFactory;

    @Autowired
    private MigrationService migrationService;

    @Autowired
    private PropertyValidator propertyValidator;

    @Autowired
    private PropertyConfiguration configs;

    @Autowired
    FuzzySearchService fuzzySearchService;

    @Autowired
    PropertyEncryptionService propertyEncryptionService;

    @PostMapping("/_create")
    public ResponseEntity<AppealResponse> create(@Valid @RequestBody AppealRequest appealRequest) {

        Appeal appeal = appealService.createAppeal(appealRequest);
        ResponseInfo resInfo = responseInfoFactory.createResponseInfoFromRequestInfo(appealRequest.getRequestInfo(), true);
        AppealResponse response = AppealResponse.builder()
                .Appeals(Arrays.asList(appeal))
                .responseInfo(resInfo)
                .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }


    @PostMapping("/_update")
    public ResponseEntity<PropertyResponse> update(@Valid @RequestBody PropertyRequest propertyRequest) {

        Property property = propertyService.updateProperty(propertyRequest);
        ResponseInfo resInfo = responseInfoFactory.createResponseInfoFromRequestInfo(propertyRequest.getRequestInfo(), true);
        PropertyResponse response = PropertyResponse.builder()
                .properties(Arrays.asList(property))
                .responseInfo(resInfo)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/_search")
    public ResponseEntity<AppealResponse> search(@Valid @RequestBody RequestInfoWrapper requestInfoWrapper,
                                                   @Valid @ModelAttribute AppealCriteria appealCriteria) {

      
    	
    	
    	List<Appeal> appeal = null;
    	Integer count = 0;
    	
    	
    	appeal = appealService.searchProperty(appealCriteria,requestInfoWrapper.getRequestInfo());
        AppealResponse response = AppealResponse.builder()
        		.responseInfo(
                        responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true))
        		.Appeals(appeal)
        		.count(count)
                .build();
        
        return new ResponseEntity<>(response, HttpStatus.OK);
    }




}
