package org.egov.validator;

import java.util.List;
import java.util.stream.Collectors;

import org.egov.repository.SchemeMasterRepository;
//import org.wms.repository.WMSSORRepository;
import org.egov.tracer.model.CustomException;
import org.egov.web.models.SchemeApplicationSearchCriteria;
import org.egov.web.models.Scheme;
import org.egov.web.models.WMSSchemeRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;


@Component
public class WMSSchemeValidator {
	
	 @Autowired
	    private SchemeMasterRepository repository;

	    public void validateSchemeApplication(WMSSchemeRequest wmsSchemeRequest) {
	    	wmsSchemeRequest.getSchemeApplications().forEach(application -> {
	            if(ObjectUtils.isEmpty(application.getTenantId()))
	                throw new CustomException("EG_WMSScheme_APP_ERR", "tenantId is mandatory for creating Scheme applications");
	        });
	    	
	    	wmsSchemeRequest.getSchemeApplications().forEach(application -> {
	            if(ObjectUtils.isEmpty(application.getSourceOfFund()))
	                throw new CustomException("EG_WMSScheme_APP_ERR", "SourceOfFund is mandatory for creating Scheme applications");
	        });
	    	
	    	
	    	wmsSchemeRequest.getSchemeApplications().forEach(application -> {
	            if(ObjectUtils.isEmpty(application.getSchemeNameEn()))
	                throw new CustomException("EG_WMSScheme_APP_ERR", "SchemeNameEn is mandatory for creating Scheme applications");
	        });
	    	
	    	wmsSchemeRequest.getSchemeApplications().forEach(application -> {
	            if(ObjectUtils.isEmpty(application.getSchemeDescription()))
	                throw new CustomException("EG_WMSScheme_APP_ERR", "SchemeDescription is mandatory for creating Scheme applications");
	        });
	    	
	    }

	    public List<Scheme> validateApplicationUpdateRequest(WMSSchemeRequest wmsSchemeRequest) {
	        List<String> ids = wmsSchemeRequest.getSchemeApplications().stream().map(Scheme::getId).collect(Collectors.toList());
	        List<Scheme> schemeApplications = repository.updateScheme(SchemeApplicationSearchCriteria.builder().Id(ids).build());
	        if(schemeApplications.size() != ids.size())
	            throw new CustomException("APPLICATION_DOES_NOT_EXIST", "One of the Scheme ids does not exist.");
	        return schemeApplications;
	    }

}
