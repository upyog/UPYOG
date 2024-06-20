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
	    	
	    	wmsWorkRequest.getWmsWorkApplications().forEach(application -> {
	            if(ObjectUtils.isEmpty(application.getWorkNo()))
	                throw new CustomException("EG_WMS_APP_ERR", "WorkNo is mandatory for creating SOR applications");
	        });
	    	
	    	wmsWorkRequest.getWmsWorkApplications().forEach(application -> {
	            if(ObjectUtils.isEmpty(application.getWorkName()))
	                throw new CustomException("EG_WMS_APP_ERR", "WorkName is mandatory for creating SOR applications");
	        });
	    	
	    	wmsWorkRequest.getWmsWorkApplications().forEach(application -> {
	            if(ObjectUtils.isEmpty(application.getProjectName()))
	                throw new CustomException("EG_WMS_APP_ERR", "ProjectName is mandatory for creating SOR applications");
	        });
	    	
	    	wmsWorkRequest.getWmsWorkApplications().forEach(application -> {
	            if(ObjectUtils.isEmpty(application.getDepartmentName()))
	                throw new CustomException("EG_WMS_APP_ERR", "DepartmentName is mandatory for creating SOR applications");
	        });
	    	
	    	wmsWorkRequest.getWmsWorkApplications().forEach(application -> {
	            if(ObjectUtils.isEmpty(application.getWorkType()))
	                throw new CustomException("EG_WMS_APP_ERR", "WorkType is mandatory for creating SOR applications");
	        });
	    	
	    	wmsWorkRequest.getWmsWorkApplications().forEach(application -> {
	            if(ObjectUtils.isEmpty(application.getWorkCategory()))
	                throw new CustomException("EG_WMS_APP_ERR", "WorkCategory is mandatory for creating SOR applications");
	        });
	    	
	    	wmsWorkRequest.getWmsWorkApplications().forEach(application -> {
	            if(ObjectUtils.isEmpty(application.getProjectPhase()))
	                throw new CustomException("EG_WMS_APP_ERR", "ProjectPhase is mandatory for creating SOR applications");
	        });
	    	wmsWorkRequest.getWmsWorkApplications().forEach(application -> {
	            if(ObjectUtils.isEmpty(application.getStartLocation()))
	                throw new CustomException("EG_WMS_APP_ERR", "StartLocation is mandatory for creating SOR applications");
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
