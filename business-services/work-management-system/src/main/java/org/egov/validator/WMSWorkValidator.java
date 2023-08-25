package org.egov.validator;

import java.util.List;
import java.util.stream.Collectors;

import org.egov.repository.WMSSORRepository;
import org.egov.repository.WMSWorkRepository;
import org.egov.tracer.model.CustomException;
import org.egov.web.models.SORApplicationSearchCriteria;
import org.egov.web.models.ScheduleOfRateApplication;
import org.egov.web.models.WMSSORRequest;
import org.egov.web.models.WMSWorkApplication;
import org.egov.web.models.WMSWorkApplicationSearchCriteria;
import org.egov.web.models.WMSWorkRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;


@Component
public class WMSWorkValidator {
	
	 @Autowired
	    private WMSWorkRepository repository;

	    public void validateWorkApplication(WMSWorkRequest wmsWorkRequest) {
	    	wmsWorkRequest.getWmsWorkApplications().forEach(application -> {
	            if(ObjectUtils.isEmpty(application.getTenantId()))
	                throw new CustomException("EG_WMS_APP_ERR", "tenantId is mandatory for creating SOR applications");
	        });
	    }

	    public List<WMSWorkApplication> validateApplicationUpdateRequest(WMSWorkRequest wmsWorkRequest) {
	        List<String> ids = wmsWorkRequest.getWmsWorkApplications().stream().map(WMSWorkApplication::getWorkId).collect(Collectors.toList());
	        List<WMSWorkApplication> wmsWorkApplications = repository.getApplications(WMSWorkApplicationSearchCriteria.builder().workId(ids).build());
	        if(wmsWorkApplications.size() != ids.size())
	            throw new CustomException("APPLICATION_DOES_NOT_EXIST", "One of the Work ids does not exist.");
	        return wmsWorkApplications;
	    }

}
