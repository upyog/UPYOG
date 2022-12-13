package org.ksmart.birth.birth.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.validation.Valid;

import org.ksmart.birth.birth.certmodel.BirthCertAppln;
import org.ksmart.birth.birth.certmodel.BirthCertApplnResponse;
import org.ksmart.birth.birth.certmodel.BirthCertRequest;
import org.ksmart.birth.birth.certmodel.BirthCertResponse;
import org.ksmart.birth.birth.certmodel.BirthCertificate;
import org.ksmart.birth.birth.model.EgBirthDtl;
import org.ksmart.birth.birth.model.SearchCriteria;
import org.ksmart.birth.birth.service.BirthService;
import org.ksmart.birth.common.contract.BirthPdfApplicationRequest;
import org.ksmart.birth.common.contract.BirthResponse;
import org.ksmart.birth.common.contract.RequestInfoWrapper;
import org.ksmart.birth.utils.ResponseInfoFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/birth")
public class BirthController {
	
	@Autowired
	BirthService birthService;
	
	@Autowired
	private ResponseInfoFactory responseInfoFactory;
	
	@Value("#{'${egov.bnd.live.citizen.tenants}'.split(',')}")
    private String[] liveCitizenTenants;
	
	private List<String> liveCitizenTenantsList = new ArrayList<String>();
	
	@PostConstruct
    public void loadTenants() {
    	liveCitizenTenantsList = Arrays.asList(liveCitizenTenants);
    }




    
	@PostMapping(value = { "/_search"})
    public ResponseEntity<BirthResponse> search(@RequestBody RequestInfoWrapper requestInfoWrapper,
                                                @Valid @ModelAttribute SearchCriteria criteria) {
		if(requestInfoWrapper.getRequestInfo().getUserInfo().getType().equalsIgnoreCase("CITIZEN") && liveCitizenTenantsList.contains(criteria.getTenantId()))
		{
			return new ResponseEntity<>(new BirthResponse(), HttpStatus.OK);
		}
        List<EgBirthDtl> birthCerts = birthService.search(criteria,requestInfoWrapper.getRequestInfo());
        BirthResponse response = BirthResponse.builder().birthCerts(birthCerts).responseInfo(
                responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true))
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(value = { "/_plainsearch"})
    public ResponseEntity<BirthCertResponse> plainSearch(@RequestBody RequestInfoWrapper requestInfoWrapper,
                                                         @Valid @ModelAttribute SearchCriteria criteria) {
        List<BirthCertificate> birthCertificates = birthService.plainSearch(criteria);
        BirthCertResponse response = BirthCertResponse.builder().birthCertificates(birthCertificates).responseInfo(
                        responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true))
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
	
	@PostMapping(value = { "/_download"})
    public ResponseEntity<BirthCertResponse> download(@RequestBody RequestInfoWrapper requestInfoWrapper,
                                                       @Valid @ModelAttribute SearchCriteria criteria) {
        System.out.println("down1");
		if(liveCitizenTenantsList.contains(criteria.getTenantId()))
		{System.out.println("down2");
			return new ResponseEntity<>(new BirthCertResponse(), HttpStatus.OK);
		}
        BirthCertificate birthCert = birthService.download(criteria,requestInfoWrapper.getRequestInfo());
        BirthCertResponse response ;
        if(birthCert.getCounter()<=0) {
            System.out.println("down2");
            response = BirthCertResponse.builder().filestoreId(birthCert.getFilestoreid()).responseInfo(
                            responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true))
                    .build();
        }
        else {System.out.println("down3");
            response = BirthCertResponse.builder().consumerCode(birthCert.getBirthCertificateNo()).tenantId(birthCert.getTenantId())
                    .responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true))
                    .build();
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
	
	@PostMapping(value = { "/_getfilestoreid"})
    public ResponseEntity<BirthCertResponse> getfilestoreid(@RequestBody RequestInfoWrapper requestInfoWrapper,
                                                       @Valid @ModelAttribute SearchCriteria criteria) {
		
        BirthCertificate birthCert = birthService.getBirthCertReqByConsumerCode(criteria,requestInfoWrapper.getRequestInfo());
        BirthCertResponse response = BirthCertResponse.builder().filestoreId(birthCert.getFilestoreid()).tenantId(criteria.getTenantId()).responseInfo(
                responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true))
                .build();
        if(null!=birthCert.getFilestoreid()) {
        	birthCert.setBirthCertificateNo(criteria.getConsumerCode());
        	birthService.updateDownloadStatus(BirthCertRequest.builder().birthCertificate(birthCert).requestInfo(requestInfoWrapper.getRequestInfo()).build());
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
	
	@PostMapping(value = { "/_searchapplications"})
    public ResponseEntity<BirthCertApplnResponse> searchApplications(@RequestBody RequestInfoWrapper requestInfoWrapper,
                                                        @ModelAttribute SearchCriteria criteria ) {
        List<BirthCertAppln> applications = birthService.searchApplications(requestInfoWrapper);
        BirthCertApplnResponse response = BirthCertApplnResponse.builder().applications(applications).responseInfo(
                responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true))
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
	
	@PostMapping(value = { "/_viewcertdata"})
    public ResponseEntity<BirthPdfApplicationRequest> viewCertificateData(@RequestBody RequestInfoWrapper requestInfoWrapper,
                                                                          @ModelAttribute SearchCriteria criteria ) {
        List<EgBirthDtl> certData = birthService.viewCertificateData(criteria);
        BirthPdfApplicationRequest response = BirthPdfApplicationRequest.builder().birthCertificate(certData).requestInfo(requestInfoWrapper.getRequestInfo())
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
	
	@PostMapping(value = { "/_viewfullcertdata"})
    public ResponseEntity<BirthPdfApplicationRequest> viewfullCertMasterData(@RequestBody RequestInfoWrapper requestInfoWrapper,
                                                        @ModelAttribute SearchCriteria criteria ) {
        List<EgBirthDtl> certData = birthService.viewfullCertMasterData(criteria,requestInfoWrapper.getRequestInfo());
        BirthPdfApplicationRequest response = BirthPdfApplicationRequest.builder().birthCertificate(certData).requestInfo(requestInfoWrapper.getRequestInfo())
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
