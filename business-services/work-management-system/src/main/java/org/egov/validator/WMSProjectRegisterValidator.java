package org.egov.validator;

import java.util.List;
import java.util.stream.Collectors;

import org.egov.repository.WMSContractorRepository;
import org.egov.repository.WMSProjectRegisterRepository;
import org.egov.repository.WMSSORRepository;
import org.egov.repository.WMSWorkRepository;
import org.egov.tracer.model.CustomException;
import org.egov.web.models.SORApplicationSearchCriteria;
import org.egov.web.models.ScheduleOfRateApplication;
import org.egov.web.models.Scheme;
import org.egov.web.models.SchemeApplicationSearchCriteria;
import org.egov.web.models.WMSContractorApplication;
import org.egov.web.models.WMSContractorApplicationSearchCriteria;
import org.egov.web.models.WMSContractorRequest;
import org.egov.web.models.WMSProjectRegisterApplication;
import org.egov.web.models.WMSProjectRegisterApplicationSearchCriteria;
import org.egov.web.models.WMSProjectRegisterRequest;
import org.egov.web.models.WMSSORRequest;
import org.egov.web.models.WMSWorkApplication;
import org.egov.web.models.WMSWorkApplicationSearchCriteria;
import org.egov.web.models.WMSWorkRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;


@Component
public class WMSProjectRegisterValidator {
	
	 @Autowired
	    private WMSProjectRegisterRepository repository;

	    public void validateProjectRegisterApplication(WMSProjectRegisterRequest wmsProjectRegisterRequest) {
	    	wmsProjectRegisterRequest.getWmsProjectRegisterApplications().forEach(application -> {
	            if(ObjectUtils.isEmpty(application.getTenantId()))
	                throw new CustomException("EG_WMS_APP_ERR", "tenantId is mandatory for creating ProjectRegister applications");
	        });
	    	
	    	wmsProjectRegisterRequest.getWmsProjectRegisterApplications().forEach(application -> {
	            if(ObjectUtils.isEmpty(application.getBillReceivedTillDate()))
	                throw new CustomException("EG_WMS_APP_ERR", "BillReceivedTillDate is mandatory for creating ProjectRegister applications");
	        });
	    	wmsProjectRegisterRequest.getWmsProjectRegisterApplications().forEach(application -> {
	            if(ObjectUtils.isEmpty(application.getEstimatedNumber()))
	                throw new CustomException("EG_WMS_APP_ERR", "EstimatedNumber is mandatory for creating ProjectRegister applications");
	        });
	    	wmsProjectRegisterRequest.getWmsProjectRegisterApplications().forEach(application -> {
	            if(ObjectUtils.isEmpty(application.getEstimatedWorkCost()))
	                throw new CustomException("EG_WMS_APP_ERR", "EstimatedWorkCost is mandatory for creating ProjectRegister applications");
	        });
			/*
			 * wmsProjectRegisterRequest.getWmsProjectRegisterApplications().forEach(
			 * application -> {
			 * if(ObjectUtils.isEmpty(application.getPaymentReceivedTillDate())) throw new
			 * CustomException("EG_WMS_APP_ERR",
			 * "PaymentReceivedTillDate is mandatory for creating ProjectRegister applications"
			 * ); });
			 */
	    	
	    	wmsProjectRegisterRequest.getWmsProjectRegisterApplications().forEach(application -> {
	            if(ObjectUtils.isEmpty(application.getProjectName()))
	                throw new CustomException("EG_WMS_APP_ERR", "ProjectName is mandatory for creating ProjectRegister applications");
	        });
	    	
	    	wmsProjectRegisterRequest.getWmsProjectRegisterApplications().forEach(application -> {
	            if(ObjectUtils.isEmpty(application.getSanctionedTenderAmount()))
	                throw new CustomException("EG_WMS_APP_ERR", "SanctionedTenderAmount is mandatory for creating ProjectRegister applications");
	        });
	    	
	    	wmsProjectRegisterRequest.getWmsProjectRegisterApplications().forEach(application -> {
	            if(ObjectUtils.isEmpty(application.getSchemeName()))
	                throw new CustomException("EG_WMS_APP_ERR", "SchemeName is mandatory for creating ProjectRegister applications");
	        });
	    	
	    	wmsProjectRegisterRequest.getWmsProjectRegisterApplications().forEach(application -> {
	            if(ObjectUtils.isEmpty(application.getWorkName()))
	                throw new CustomException("EG_WMS_APP_ERR", "WorkName is mandatory for creating ProjectRegister applications");
	        });
	    	
	    	wmsProjectRegisterRequest.getWmsProjectRegisterApplications().forEach(application -> {
	            if(ObjectUtils.isEmpty(application.getWorkType()))
	                throw new CustomException("EG_WMS_APP_ERR", "WorkType is mandatory for creating ProjectRegister applications");
	        });
	    	
	    	wmsProjectRegisterRequest.getWmsProjectRegisterApplications().forEach(application -> {
	            if(ObjectUtils.isEmpty(application.getStatusName()))
	                throw new CustomException("EG_WMS_APP_ERR", "StatusName is mandatory for creating ProjectRegister applications");
	        });
	    }

		public List<WMSProjectRegisterApplication> validateApplicationUpdateRequest(WMSProjectRegisterRequest projectRegisterRequest) {
			List<String> ids = projectRegisterRequest.getWmsProjectRegisterApplications().stream().map(WMSProjectRegisterApplication::getRegisterId).collect(Collectors.toList());
	        List<WMSProjectRegisterApplication> projectRegisterApplications = repository.getApplications(WMSProjectRegisterApplicationSearchCriteria.builder().registerId(ids).build());
	        if(projectRegisterApplications.size() != ids.size())
	            throw new CustomException("APPLICATION_DOES_NOT_EXIST", "One of the ProjectRegister ids does not exist.");
	        return projectRegisterApplications;
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
