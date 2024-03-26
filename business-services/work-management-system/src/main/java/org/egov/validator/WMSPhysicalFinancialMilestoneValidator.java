package org.egov.validator;

import java.util.List;
import java.util.stream.Collectors;

import org.egov.repository.WMSContractorRepository;
import org.egov.repository.WMSPhysicalFinancialMilestoneRepository;
import org.egov.repository.WMSSORRepository;
import org.egov.repository.WMSWorkRepository;
import org.egov.tracer.model.CustomException;
import org.egov.web.models.SORApplicationSearchCriteria;
import org.egov.web.models.ScheduleOfRateApplication;
import org.egov.web.models.WMSContractorApplication;
import org.egov.web.models.WMSContractorApplicationSearchCriteria;
import org.egov.web.models.WMSContractorRequest;
import org.egov.web.models.WMSPhysicalFinancialMilestoneApplication;
import org.egov.web.models.WMSPhysicalFinancialMilestoneApplicationSearchCriteria;
import org.egov.web.models.WMSPhysicalFinancialMilestoneRequest;
import org.egov.web.models.WMSSORRequest;
import org.egov.web.models.WMSWorkApplication;
import org.egov.web.models.WMSWorkApplicationSearchCriteria;
import org.egov.web.models.WMSWorkRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;


@Component
public class WMSPhysicalFinancialMilestoneValidator {
	
	 @Autowired
	    private WMSPhysicalFinancialMilestoneRepository repository;

	    public void validatePhysicalFinancialMilestoneApplication(WMSPhysicalFinancialMilestoneRequest wmsPhysicalFinancialMilestoneRequest) {
	    	wmsPhysicalFinancialMilestoneRequest.getWmsPhysicalFinancialMilestoneApplications().forEach(application -> {
	            if(ObjectUtils.isEmpty(application.getTenantId()))
	                throw new CustomException("EG_WMS_APP_ERR", "tenantId is mandatory for creating Physical Financial Milestone applications");
	        });
	    	
	    	wmsPhysicalFinancialMilestoneRequest.getWmsPhysicalFinancialMilestoneApplications().forEach(application -> {
	            if(ObjectUtils.isEmpty(application.getProjectName()))
	                throw new CustomException("EG_WMS_APP_ERR", "ProjectName is mandatory for creating Physical Financial Milestone applications");
	        });
	    	
	    	wmsPhysicalFinancialMilestoneRequest.getWmsPhysicalFinancialMilestoneApplications().forEach(application -> {
	            if(ObjectUtils.isEmpty(application.getWorkName()))
	                throw new CustomException("EG_WMS_APP_ERR", "WorkName is mandatory for creating Physical Financial Milestone applications");
	        });
	    	
	    	wmsPhysicalFinancialMilestoneRequest.getWmsPhysicalFinancialMilestoneApplications().forEach(application -> {
	            if(ObjectUtils.isEmpty(application.getMilestoneName()))
	                throw new CustomException("EG_WMS_APP_ERR", "MilestoneName is mandatory for creating Physical Financial Milestone applications");
	        });
	    	
			/*
			 * wmsPhysicalFinancialMilestoneRequest.
			 * getWmsPhysicalFinancialMilestoneApplications().forEach(application -> {
			 * if(ObjectUtils.isEmpty(application.getPercentageWeightage())) throw new
			 * CustomException("EG_WMS_APP_ERR",
			 * "PercentageWeightage is mandatory for creating Physical Financial Milestone applications"
			 * ); });
			 */
	    	
	    	
	    	
	    }

		public List<WMSPhysicalFinancialMilestoneApplication> validateApplicationUpdateRequest(
				WMSPhysicalFinancialMilestoneRequest physicalFinancialMilestoneRequest) {
			List<String> ids = physicalFinancialMilestoneRequest.getWmsPhysicalFinancialMilestoneApplications().stream().map(WMSPhysicalFinancialMilestoneApplication::getMilestoneId).collect(Collectors.toList());
	        List<WMSPhysicalFinancialMilestoneApplication> physicalFinancialMilestoneApplications = repository.getApplications(WMSPhysicalFinancialMilestoneApplicationSearchCriteria.builder().milestoneId(ids).build());
	        if(physicalFinancialMilestoneApplications.size() != ids.size())
	            throw new CustomException("APPLICATION_DOES_NOT_EXIST", "One of the Physical Financial Milestone ids does not exist.");
	        return physicalFinancialMilestoneApplications;
		}

		/*
		 * public List<WMSWorkApplication>
		 * validateApplicationUpdateRequest(WMSWorkRequest wmsWorkRequest) {
		 * List<Integer> ids =
		 * wmsWorkRequest.getWmsWorkApplications().stream().map(WMSWorkApplication::
		 * getWorkId).collect(Collectors.toList()); List<WMSWorkApplication>
		 * wmsWorkApplications =
		 * repository.getApplications(WMSWorkApplicationSearchCriteria.builder().workId(
		 * ids).build()); if(wmsWorkApplications.size() != ids.size()) throw new
		 * CustomException("APPLICATION_DOES_NOT_EXIST",
		 * "One of the Work ids does not exist."); return wmsWorkApplications; }
		 */

}
