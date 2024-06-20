package org.egov.validator;

import java.util.List;
import java.util.stream.Collectors;

import org.egov.repository.WMSSORRepository;
import org.egov.tracer.model.CustomException;
import org.egov.web.models.SORApplicationSearchCriteria;
import org.egov.web.models.ScheduleOfRateApplication;
import org.egov.web.models.WMSSORRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;


@Component
public class WMSSORValidator {
	
	 @Autowired
	    private WMSSORRepository repository;

	    public void validateSORApplication(WMSSORRequest wmsSORRequest) {
	    	wmsSORRequest.getScheduleOfRateApplications().forEach(application -> {
	            if(ObjectUtils.isEmpty(application.getTenantId()))
	                throw new CustomException("EG_WMS_APP_ERR", "tenantId is mandatory for creating SOR applications");
	        });
	    	
	    	wmsSORRequest.getScheduleOfRateApplications().forEach(application -> {
	            if(ObjectUtils.isEmpty(application.getChapter()))
	                throw new CustomException("EG_WMS_APP_ERR", "Chapter is mandatory for creating SOR applications");
	        });
	    	
	    	wmsSORRequest.getScheduleOfRateApplications().forEach(application -> {
	            if(ObjectUtils.isEmpty(application.getItemNo()))
	                throw new CustomException("EG_WMS_APP_ERR", "ItemNo is mandatory for creating SOR applications");
	        });
	    	
	    	wmsSORRequest.getScheduleOfRateApplications().forEach(application -> {
	            if(ObjectUtils.isEmpty(application.getDescOfItem()))
	                throw new CustomException("EG_WMS_APP_ERR", "DescOfItem is mandatory for creating SOR applications");
	        });
	    	
	    	wmsSORRequest.getScheduleOfRateApplications().forEach(application -> {
	            if(ObjectUtils.isEmpty(application.getUnit()))
	                throw new CustomException("EG_WMS_APP_ERR", "Unit is mandatory for creating SOR applications");
	        });
	    	
	    	wmsSORRequest.getScheduleOfRateApplications().forEach(application -> {
	            if(ObjectUtils.isEmpty(application.getRate()))
	                throw new CustomException("EG_WMS_APP_ERR", "Rate is mandatory for creating SOR applications");
	        });
	    }

	    public List<ScheduleOfRateApplication> validateApplicationUpdateRequest(WMSSORRequest wmsSORRequest) {
	        String ids = wmsSORRequest.getScheduleOfRateApplications().get(0).getSorName();
	        List<ScheduleOfRateApplication> sorApplications = repository.getApplications(SORApplicationSearchCriteria.builder().sorName(ids).build());
	        if(sorApplications.size() == 0)
	            throw new CustomException("APPLICATION_DOES_NOT_EXIST", "One of the SOR data does not exist.");
	        return sorApplications;
	    }

}
