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
	            if(ObjectUtils.isEmpty(application.getSorName()))
	                throw new CustomException("EG_WMS_APP_ERR", "SOR Name is mandatory for creating SOR applications");
	        });
	    }

	    public List<ScheduleOfRateApplication> validateApplicationUpdateRequest(WMSSORRequest wmsSORRequest) {
	        List<Integer> ids = wmsSORRequest.getScheduleOfRateApplications().stream().map(ScheduleOfRateApplication::getSorId).collect(Collectors.toList());
	        List<ScheduleOfRateApplication> sorApplications = repository.getApplications(SORApplicationSearchCriteria.builder().sorId(ids).build());
	        if(sorApplications.size() != ids.size())
	            throw new CustomException("APPLICATION_DOES_NOT_EXIST", "One of the SOR ids does not exist.");
	        return sorApplications;
	    }

}
