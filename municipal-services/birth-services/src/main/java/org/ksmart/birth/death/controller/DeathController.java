package org.ksmart.birth.death.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.validation.Valid;

import org.ksmart.birth.common.contract.DeathPdfApplicationRequest;
import org.ksmart.birth.common.contract.DeathResponse;
import org.ksmart.birth.common.contract.RequestInfoWrapper;
import org.ksmart.birth.death.certmodel.DeathCertAppln;
import org.ksmart.birth.death.certmodel.DeathCertApplnResponse;
import org.ksmart.birth.death.certmodel.DeathCertRequest;
import org.ksmart.birth.death.certmodel.DeathCertResponse;
import org.ksmart.birth.death.certmodel.DeathCertificate;
import org.ksmart.birth.death.model.EgDeathDtl;
import org.ksmart.birth.death.model.SearchCriteria;
import org.ksmart.birth.death.service.DeathService;
import org.ksmart.birth.utils.ResponseInfoFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/death")
public class DeathController {
	
	@Autowired
	DeathService deathService;
	
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
    public ResponseEntity<DeathResponse> search(@RequestBody RequestInfoWrapper requestInfoWrapper,
                                                @Valid @ModelAttribute SearchCriteria criteria) {
		if(requestInfoWrapper.getRequestInfo().getUserInfo().getType().equalsIgnoreCase("CITIZEN") && liveCitizenTenantsList.contains(criteria.getTenantId()))
		{
			return new ResponseEntity<>(new DeathResponse(), HttpStatus.OK);
		}
        List<EgDeathDtl> deathCerts = deathService.search(criteria,requestInfoWrapper.getRequestInfo());
        DeathResponse response = DeathResponse.builder().deathCerts(deathCerts).responseInfo(
                responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true))
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(value = { "/_plainsearch"})
    public ResponseEntity<DeathCertResponse> plainSearch(@RequestBody RequestInfoWrapper requestInfoWrapper,
                                                         @Valid @ModelAttribute SearchCriteria criteria) {
        List<DeathCertificate> deathCertificates = deathService.plainSearch(criteria);
        DeathCertResponse response = DeathCertResponse.builder().deathCertificates(deathCertificates).responseInfo(
                        responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true))
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
	
	@PostMapping(value = { "/_download"})
    public ResponseEntity<DeathCertResponse> download(@RequestBody RequestInfoWrapper requestInfoWrapper,
                                                       @Valid @ModelAttribute SearchCriteria criteria) {
		if(liveCitizenTenantsList.contains(criteria.getTenantId()))
		{
			return new ResponseEntity<>(new DeathCertResponse(), HttpStatus.OK);
		}
        DeathCertificate deathCert = deathService.download(criteria,requestInfoWrapper.getRequestInfo());
        DeathCertResponse response ;
        if(deathCert.getCounter()<=0)
        	response = DeathCertResponse.builder().filestoreId(deathCert.getFilestoreid()).responseInfo(
                responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true))
                .build();
        else
        	response = DeathCertResponse.builder().consumerCode(deathCert.getDeathCertificateNo()).tenantId(deathCert.getTenantId())
        			.responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true))
                    .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
	
	@PostMapping(value = { "/_getfilestoreid"})
    public ResponseEntity<DeathCertResponse> getfilestoreid(@RequestBody RequestInfoWrapper requestInfoWrapper,
                                                       @Valid @ModelAttribute SearchCriteria criteria) {
		
        DeathCertificate deathCert = deathService.getDeathCertReqByConsumerCode(criteria,requestInfoWrapper.getRequestInfo());
        DeathCertResponse response = DeathCertResponse.builder().filestoreId(deathCert.getFilestoreid()).tenantId(criteria.getTenantId()).responseInfo(
                responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true))
                .build();
        if(null!=deathCert.getFilestoreid()) {
        	deathCert.setDeathCertificateNo(criteria.getConsumerCode());
        	deathService.updateDownloadStatus(DeathCertRequest.builder().deathCertificate(deathCert).requestInfo(requestInfoWrapper.getRequestInfo()).build());
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
	
	@PostMapping(value = { "/_searchapplications"})
    public ResponseEntity<DeathCertApplnResponse> searchApplications(@RequestBody RequestInfoWrapper requestInfoWrapper,
                                                        @ModelAttribute SearchCriteria criteria ) {
        List<DeathCertAppln> applications = deathService.searchApplications(requestInfoWrapper);
        DeathCertApplnResponse response = DeathCertApplnResponse.builder().applications(applications).responseInfo(
                responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true))
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
	
	@PostMapping(value = { "/_viewcertdata"})
    public ResponseEntity<DeathPdfApplicationRequest> viewCertificateData(@RequestBody RequestInfoWrapper requestInfoWrapper,
                                                                          @ModelAttribute SearchCriteria criteria ) {
        List<EgDeathDtl> certData = deathService.viewCertificateData(criteria);
        DeathPdfApplicationRequest response = DeathPdfApplicationRequest.builder().deathCertificate(certData).requestInfo(requestInfoWrapper.getRequestInfo())
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
	
	@PostMapping(value = { "/_viewfullcertdata"})
    public ResponseEntity<DeathPdfApplicationRequest> viewfullCertMasterData(@RequestBody RequestInfoWrapper requestInfoWrapper,
                                                        @ModelAttribute SearchCriteria criteria ) {
        List<EgDeathDtl> certData = deathService.viewfullCertMasterData(criteria,requestInfoWrapper.getRequestInfo());
        DeathPdfApplicationRequest response = DeathPdfApplicationRequest.builder().deathCertificate(certData).requestInfo(requestInfoWrapper.getRequestInfo())
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
