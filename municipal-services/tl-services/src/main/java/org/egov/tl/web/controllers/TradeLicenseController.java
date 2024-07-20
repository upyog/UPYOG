package org.egov.tl.web.controllers;


import static org.egov.tl.util.TLConstants.businessService_TL;

import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.egov.tl.service.TradeLicenseService;
import org.egov.tl.service.notification.PaymentNotificationService;
import org.egov.tl.service.notification.TLNotificationService;
import org.egov.tl.util.ResponseInfoFactory;
import org.egov.tl.util.TLConstants;
import org.egov.tl.web.models.ApplicationStatusChangeRequest;
import org.egov.tl.web.models.RequestInfoWrapper;
import org.egov.tl.web.models.TradeLicense;
import org.egov.tl.web.models.TradeLicenseRequest;
import org.egov.tl.web.models.TradeLicenseResponse;
import org.egov.tl.web.models.TradeLicenseSearchCriteria;
import org.egov.tl.web.models.UpdateTLStatusCriteriaRequest;
import org.egov.tl.web.models.contract.ProcessInstanceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

@RestController	
@CrossOrigin(origins="*")
    @RequestMapping("/v1")
    public class TradeLicenseController {

        private final ObjectMapper objectMapper;

        private final HttpServletRequest request;

        private final TradeLicenseService tradeLicenseService;

        private final ResponseInfoFactory responseInfoFactory;

        private final PaymentNotificationService paymentNotificationService;

        private final TLNotificationService tlNotificationService;

    @Autowired
    public TradeLicenseController(ObjectMapper objectMapper, HttpServletRequest request, TradeLicenseService tradeLicenseService,
                                  ResponseInfoFactory responseInfoFactory, PaymentNotificationService paymentNotificationService, TLNotificationService tlNotificationService) {
        this.objectMapper = objectMapper;
        this.request = request;
        this.tradeLicenseService = tradeLicenseService;
        this.responseInfoFactory = responseInfoFactory;
        this.paymentNotificationService = paymentNotificationService;
        this.tlNotificationService = tlNotificationService;
    }




    @PostMapping({"/{servicename}/_create", "/_create"})
    public ResponseEntity<TradeLicenseResponse> create(@Valid @RequestBody TradeLicenseRequest tradeLicenseRequest,
                                                       @PathVariable(required = false) String servicename) {
        List<TradeLicense> licenses = tradeLicenseService.create(tradeLicenseRequest, servicename);
        TradeLicenseResponse response = TradeLicenseResponse.builder().licenses(licenses).responseInfo(
                responseInfoFactory.createResponseInfoFromRequestInfo(tradeLicenseRequest.getRequestInfo(), true))
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(value = {"/{servicename}/_search", "/_search"}, method = RequestMethod.POST)
    public ResponseEntity<TradeLicenseResponse> search(@Valid @RequestBody RequestInfoWrapper requestInfoWrapper,
                                                       @Valid @ModelAttribute TradeLicenseSearchCriteria criteria,
                                                       @PathVariable(required = false) String servicename
            , @RequestHeader HttpHeaders headers) {
        List<TradeLicense> licenses = tradeLicenseService.search(criteria, requestInfoWrapper.getRequestInfo(), servicename, headers);
        
        int count = tradeLicenseService.countLicenses(criteria, requestInfoWrapper.getRequestInfo(), servicename, headers);
        
        int applicationsIssued = tradeLicenseService.countApplications(criteria, requestInfoWrapper.getRequestInfo(), servicename, headers).get(TLConstants.ISSUED_COUNT);
        int applicationsRenewed = tradeLicenseService.countApplications(criteria, requestInfoWrapper.getRequestInfo(), servicename, headers).get(TLConstants.RENEWED_COUNT);
        int validity = tradeLicenseService.getApplicationValidity();

        TradeLicenseResponse response = TradeLicenseResponse.builder().licenses(licenses).responseInfo(
                responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true)).count(count).applicationsIssued(applicationsIssued)
        		.applicationsRenewed(applicationsRenewed).validity(validity).build();
        tradeLicenseService.processResponse(response);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(value = {"/{servicename}/_update", "/_update"}, method = RequestMethod.POST)
    public ResponseEntity<TradeLicenseResponse> update(@RequestBody TradeLicenseRequest tradeLicenseRequest,
                                                       @PathVariable(required = false) String servicename) {
    	
        List<TradeLicense> licenses = tradeLicenseService.update(tradeLicenseRequest, servicename);

        TradeLicenseResponse response = TradeLicenseResponse.builder().licenses(licenses).responseInfo(
                responseInfoFactory.createResponseInfoFromRequestInfo(tradeLicenseRequest.getRequestInfo(), true))
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

	@RequestMapping(value = {"/{servicename}/{jobname}/_batch", "/_batch"}, method = RequestMethod.POST)
    public ResponseEntity sendReminderSMS(@Valid @RequestBody RequestInfoWrapper requestInfoWrapper,
                                          @PathVariable(required = false) String servicename,
                                          @PathVariable(required = true) String jobname) {

        tradeLicenseService.runJob(servicename, jobname, requestInfoWrapper.getRequestInfo());

        return new ResponseEntity(HttpStatus.ACCEPTED);
    }

    @RequestMapping(value="/_plainsearch", method = RequestMethod.POST)
    public ResponseEntity<TradeLicenseResponse> plainsearch(@Valid @RequestBody RequestInfoWrapper requestInfoWrapper,
                                                            @Valid @ModelAttribute TradeLicenseSearchCriteria criteria){

        List<TradeLicense> licenses = tradeLicenseService.plainSearch(criteria,requestInfoWrapper.getRequestInfo());

        TradeLicenseResponse response = TradeLicenseResponse.builder().licenses(licenses).responseInfo(
                responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true))
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/_test")
    public ResponseEntity test(@Valid @RequestBody HashMap<String, Object> record){
        paymentNotificationService.processBusinessService(record, businessService_TL);
        return new ResponseEntity(HttpStatus.OK);
    }

    @PostMapping("/_test1")
    public ResponseEntity test1(@Valid @RequestBody TradeLicenseRequest tradeLicenseRequest){
        tlNotificationService.process(tradeLicenseRequest);
        return new ResponseEntity(HttpStatus.OK);
    }
    
    
    @PostMapping("/update/state")
    public ResponseEntity<ProcessInstanceResponse> updateStateWf(@RequestBody UpdateTLStatusCriteriaRequest updateTLStatusCriteriaRequest){
    	ProcessInstanceResponse processInstanceResponse = tradeLicenseService.updateState(updateTLStatusCriteriaRequest);
        return new ResponseEntity(processInstanceResponse , HttpStatus.OK);
    }
    
    @PostMapping("/updateApplicationAppliedStatus")
    public ResponseEntity<?> updateStateOfApplication(@RequestBody ApplicationStatusChangeRequest applicationStatusChangeRequest){
    	ApplicationStatusChangeRequest applicationStatusChangeRequest2 = tradeLicenseService.updateStateOfApplication(applicationStatusChangeRequest);
        return new ResponseEntity(applicationStatusChangeRequest2 , HttpStatus.OK);
    }

    @PostMapping("/testPdfCreateAndUpload")
    public ResponseEntity<Resource> testPdfCreateAndUpload(@Valid @RequestBody TradeLicenseRequest tradeLicenseRequest){
    	Resource object = tradeLicenseService.createNoSavePDF(tradeLicenseRequest.getLicenses().get(0)
    														, tradeLicenseRequest.getRequestInfo());
    	

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "generated.pdf");

        return new ResponseEntity(object, headers, HttpStatus.OK);
    }

}
