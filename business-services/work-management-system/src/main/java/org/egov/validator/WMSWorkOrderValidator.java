package org.egov.validator;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.egov.repository.WMSContractorRepository;
import org.egov.repository.WMSSORRepository;
import org.egov.repository.WMSWorkOrderRepository;
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
import org.egov.web.models.WMSWorkOrderApplication;
import org.egov.web.models.WMSWorkOrderApplicationSearchCriteria;
import org.egov.web.models.WMSWorkOrderRequest;
import org.egov.web.models.WMSWorkRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;


@Component
public class WMSWorkOrderValidator {
	
	 @Autowired
	    private WMSWorkOrderRepository repository;

	    public void validateWorkOrderApplication(WMSWorkOrderRequest wmsWorkOrderRequest) {
	    	wmsWorkOrderRequest.getWmsWorkOrderApplications().forEach(application -> {
	            if(ObjectUtils.isEmpty(application.getTenantId()))
	                throw new CustomException("EG_WMS_APP_ERR", "tenantId is mandatory for creating WorkOrder applications");
	        });
	    	
	    	wmsWorkOrderRequest.getWmsWorkOrderApplications().forEach(application -> {
	            if(ObjectUtils.isEmpty(application.getWorkOrderDate()))
	                throw new CustomException("EG_WMS_APP_ERR", "WorkOrderDate is mandatory for creating WorkOrder applications");
	        });
	    	
	    	wmsWorkOrderRequest.getWmsWorkOrderApplications().forEach(application -> {
	            if(ObjectUtils.isEmpty(application.getAgreementNo()))
	                throw new CustomException("EG_WMS_APP_ERR", "AgreementNo is mandatory for creating WorkOrder applications");
	        });
	    	
	    	wmsWorkOrderRequest.getWmsWorkOrderApplications().forEach(application -> {
	            if(ObjectUtils.isEmpty(application.getDateOfCommencement()))
	                throw new CustomException("EG_WMS_APP_ERR", "DateOfCommencement is mandatory for creating WorkOrder applications");
	        });
	    	
	    	wmsWorkOrderRequest.getWmsWorkOrderApplications().forEach(application -> {
	            if(ObjectUtils.isEmpty(application.getWorkAssignee()))
	                throw new CustomException("EG_WMS_APP_ERR", "WorkAssignee is mandatory for creating WorkOrder applications");
	        });
	    }

		public List<WMSWorkOrderApplication> validateApplicationUpdateRequest(
				 WMSWorkOrderRequest workOrderRequest) {
			List<String> ids = workOrderRequest.getWmsWorkOrderApplications().stream().map(WMSWorkOrderApplication::getWorkOrderId).collect(Collectors.toList());
	        List<WMSWorkOrderApplication> workOrderApplications = repository.getApplications(WMSWorkOrderApplicationSearchCriteria.builder().workOrderId(ids).build());
	        if(workOrderApplications.size() != ids.size())
	            throw new CustomException("APPLICATION_DOES_NOT_EXIST", "One of the WorkOrder ids does not exist.");
	        return workOrderApplications;
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
