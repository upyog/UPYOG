package org.egov.validator;

import java.util.List;
import java.util.stream.Collectors;

import org.egov.repository.WMSContractorRepository;
import org.egov.repository.WMSSORRepository;
import org.egov.repository.WMSWorkAwardApprovalRepository;
import org.egov.repository.WMSWorkRepository;
import org.egov.tracer.model.CustomException;
import org.egov.web.models.SORApplicationSearchCriteria;
import org.egov.web.models.ScheduleOfRateApplication;
import org.egov.web.models.WMSContractorApplication;
import org.egov.web.models.WMSContractorApplicationSearchCriteria;
import org.egov.web.models.WMSContractorRequest;
import org.egov.web.models.WMSSORRequest;
import org.egov.web.models.WMSWorkApplication;
import org.egov.web.models.WMSWorkApplicationSearchCriteria;
import org.egov.web.models.WMSWorkAwardApprovalApplication;
import org.egov.web.models.WMSWorkAwardApprovalApplicationSearchCriteria;
import org.egov.web.models.WMSWorkAwardApprovalRequest;
import org.egov.web.models.WMSWorkRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;


@Component
public class WMSWorkAwardApprovalValidator {
	
	 @Autowired
	    private WMSWorkAwardApprovalRepository repository;

	    public void validateWorkAwardApprovalApplication(WMSWorkAwardApprovalRequest wmsWorkAwardApprovalRequest) {
	    	wmsWorkAwardApprovalRequest.getWmsWorkAwardApprovalApplications().forEach(application -> {
	            if(ObjectUtils.isEmpty(application.getTenantId()))
	                throw new CustomException("EG_WMS_APP_ERR", "tenantId is mandatory for creating Work Award Approval applications");
	        });
	    	
	    	wmsWorkAwardApprovalRequest.getWmsWorkAwardApprovalApplications().forEach(application -> {
	            if(ObjectUtils.isEmpty(application.getWorkName()))
	                throw new CustomException("EG_WMS_APP_ERR", "WorkName is mandatory for creating Work Award Approval applications");
	        });
	    	
	    	wmsWorkAwardApprovalRequest.getWmsWorkAwardApprovalApplications().forEach(application -> {
	            if(ObjectUtils.isEmpty(application.getWorkNo()))
	                throw new CustomException("EG_WMS_APP_ERR", "WorkNo is mandatory for creating Work Award Approval applications");
	        });
	    	
	    	wmsWorkAwardApprovalRequest.getWmsWorkAwardApprovalApplications().forEach(application -> {
	            if(ObjectUtils.isEmpty(application.getAcceptedWorkCost()))
	                throw new CustomException("EG_WMS_APP_ERR", "AcceptedWorkCost is mandatory for creating Work Award Approval applications");
	        });
	    	
	    	wmsWorkAwardApprovalRequest.getWmsWorkAwardApprovalApplications().forEach(application -> {
	            if(ObjectUtils.isEmpty(application.getContractorName()))
	                throw new CustomException("EG_WMS_APP_ERR", "ContractorName is mandatory for creating Work Award Approval applications");
	        });
	    	
	    	wmsWorkAwardApprovalRequest.getWmsWorkAwardApprovalApplications().forEach(application -> {
	            if(ObjectUtils.isEmpty(application.getNoOfDaysForAgreement()))
	                throw new CustomException("EG_WMS_APP_ERR", "NoOfDaysForAgreement is mandatory for creating Work Award Approval applications");
	        });
	    	
	    	wmsWorkAwardApprovalRequest.getWmsWorkAwardApprovalApplications().forEach(application -> {
	            if(ObjectUtils.isEmpty(application.getAwardStatus()))
	                throw new CustomException("EG_WMS_APP_ERR", "AwardStatus is mandatory for creating Work Award Approval applications");
	        });
	    	
	    	
	    	
	    	
	    }

		public List<WMSWorkAwardApprovalApplication> validateApplicationUpdateRequest(
				WMSWorkAwardApprovalRequest workAwardApprovalRequest) {
			List<String> ids = workAwardApprovalRequest.getWmsWorkAwardApprovalApplications().stream().map(WMSWorkAwardApprovalApplication::getWorkAwardId).collect(Collectors.toList());
	        List<WMSWorkAwardApprovalApplication> workAwardApprovalApplications = repository.getApplications(WMSWorkAwardApprovalApplicationSearchCriteria.builder().workAwardId(ids).build());
	        if(workAwardApprovalApplications.size() != ids.size())
	            throw new CustomException("APPLICATION_DOES_NOT_EXIST", "One of the Work Award Approval ids does not exist.");
	        return workAwardApprovalApplications;
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
