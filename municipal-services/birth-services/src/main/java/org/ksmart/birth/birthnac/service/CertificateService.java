package org.ksmart.birth.birthnac.service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.egov.common.contract.request.RequestInfo;
 
import org.ksmart.birth.web.model.SearchCriteria;
import org.ksmart.birth.web.model.birthnac.NacApplication;
import org.ksmart.birth.web.model.birthnac.NacDetailRequest;
import org.ksmart.birth.utils.MdmsUtil;
import org.ksmart.birth.birthnac.repository.NacRepository;
import org.ksmart.birth.birthnac.validator.NacApplicationValidator;
import org.ksmart.birth.web.model.birthnac.certificate.CertificateResponse;
import org.ksmart.birth.workflow.WorkflowIntegratorNac;
import org.ksmart.birth.web.model.birthnac.certificate.CertificateDetails;
import org.ksmart.birth.web.model.birthnac.certificate.CertificateRequest;
import org.ksmart.birth.web.model.birthnac.NacSearchCriteria;

import org.ksmart.birth.birthnac.repository.NacRepository;
import org.ksmart.birth.config.BirthConfiguration;


import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@Service
public class CertificateService {
	
 
	 private final NacRepository repository;
     private final WorkflowIntegratorNac workflowIntegrator;
     private final MdmsUtil mdmsUtil;
     private final BirthConfiguration birthDeathConfiguration;
     
	    @Autowired
	    CertificateService(NacRepository repository, MdmsUtil mdmsUtil, BirthConfiguration birthDeathConfiguration,
	    		WorkflowIntegratorNac workflowIntegrator){
	 
	        this.repository = repository;
	        this.mdmsUtil = mdmsUtil;
	        this.workflowIntegrator  = workflowIntegrator;
	        this.birthDeathConfiguration =birthDeathConfiguration;
	      
	    }

 
	
//	  public CertificateRequest createCertificateRequest(RequestInfo requestInfo, NacSearchCriteria criteria) {
//		  
//		  List<NacApplication> nacDetail = repository.searchNacDetails(request,criteria);
//		  
//		  final String embeddedUrl = buildEmbeddedUrl(nacDetail);
//		  
//		  return certificateRequest;
//		  
//	  }
//	  
//	  private String buildEmbeddedUrl(final NacApplication nacDetail) {
//	        final String uiHostCert = birthDeathConfiguration.getUiAppHost();
//
//	        String resCertPath = birthDeathConfiguration.getNacCertLink();
//	        resCertPath = resCertPath.replace("$id", nacDetail.getId());
//	        resCertPath = resCertPath.replace("$tenantId",
//	        		nacDetail.getTenantId());
//	        resCertPath = resCertPath.replace("$regNo",
//	        		nacDetail.getFileNumber());
//
//	        final String embeddedUrl = uiHostCert + resCertPath;
//
//	        return getShortenedUrl(embeddedUrl);
//	    }
	  

}
