package org.egov.validator;

import java.util.List;
import java.util.stream.Collectors;

import org.egov.repository.WMSContractorRepository;
import org.egov.repository.WMSSORRepository;
import org.egov.repository.WMSWorkEstimationRepository;
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
import org.egov.web.models.WMSWorkEstimationApplication;
import org.egov.web.models.WMSWorkEstimationApplicationSearchCriteria;
import org.egov.web.models.WMSWorkEstimationRequest;
import org.egov.web.models.WMSWorkRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;


@Component
public class WMSWorkEstimationValidator {
	
	 @Autowired
	    private WMSWorkEstimationRepository repository;

	    public void validateWorkEstimationApplication(WMSWorkEstimationRequest wmsWorkEstimationRequest) {
	    	wmsWorkEstimationRequest.getWmsWorkEstimationApplications().forEach(application -> {
	            if(ObjectUtils.isEmpty(application.getTenantId()))
	                throw new CustomException("EG_WMS_APP_ERR", "tenantId is mandatory for creating Work Estimation applications");
	        });
	    	
	    	wmsWorkEstimationRequest.getWmsWorkEstimationApplications().forEach(application -> {
	            if(ObjectUtils.isEmpty(application.getProjectName()))
	                throw new CustomException("EG_WMS_APP_ERR", "ProjectName is mandatory for creating Work Estimation applications");
	        });
	    	
	    	wmsWorkEstimationRequest.getWmsWorkEstimationApplications().forEach(application -> {
	            if(ObjectUtils.isEmpty(application.getWorkName()))
	                throw new CustomException("EG_WMS_APP_ERR", "WorkName is mandatory for creating Work Estimation applications");
	        });
	    	
	    	wmsWorkEstimationRequest.getWmsWorkEstimationApplications().forEach(application -> {
	            if(ObjectUtils.isEmpty(application.getEstimateType()))
	                throw new CustomException("EG_WMS_APP_ERR", "EstimateType is mandatory for creating Work Estimation applications");
	        });
	    	
	    	wmsWorkEstimationRequest.getWmsWorkEstimationApplications().forEach(application -> {
	            if(ObjectUtils.isEmpty(application.getChapter()))
	                throw new CustomException("EG_WMS_APP_ERR", "Chapter is mandatory for creating Work Estimation applications");
	        });
	    	
	    	wmsWorkEstimationRequest.getWmsWorkEstimationApplications().forEach(application -> {
	            if(ObjectUtils.isEmpty(application.getItemNo()))
	                throw new CustomException("EG_WMS_APP_ERR", "ItemNo is mandatory for creating Work Estimation applications");
	        });
	    	
	    	wmsWorkEstimationRequest.getWmsWorkEstimationApplications().forEach(application -> {
	            if(ObjectUtils.isEmpty(application.getDescriptionOfTheItem()))
	                throw new CustomException("EG_WMS_APP_ERR", "DesriptionOfTheItem is mandatory for creating Work Estimation applications");
	        });
	    	
	    	wmsWorkEstimationRequest.getWmsWorkEstimationApplications().forEach(application -> {
	            if(ObjectUtils.isEmpty(application.getUnit()))
	                throw new CustomException("EG_WMS_APP_ERR", "Unit is mandatory for creating Work Estimation applications");
	        });
	    	
	    	wmsWorkEstimationRequest.getWmsWorkEstimationApplications().forEach(application -> {
	            if(ObjectUtils.isEmpty(application.getRate()))
	                throw new CustomException("EG_WMS_APP_ERR", "Rate is mandatory for creating Work Estimation applications");
	        });
	    	
	    	wmsWorkEstimationRequest.getWmsWorkEstimationApplications().forEach(application -> {
	            if(ObjectUtils.isEmpty(application.getEstimateAmount()))
	                throw new CustomException("EG_WMS_APP_ERR", "EstimateAmount is mandatory for creating Work Estimation applications");
	        });
	    	
	    	wmsWorkEstimationRequest.getWmsWorkEstimationApplications().forEach(application -> {
	            if(ObjectUtils.isEmpty(application.getSerialNo()))
	                throw new CustomException("EG_WMS_APP_ERR", "SerialNo is mandatory for creating Work Estimation applications");
	        });
	    	
	    	wmsWorkEstimationRequest.getWmsWorkEstimationApplications().forEach(application -> {
	            if(ObjectUtils.isEmpty(application.getParticularsOfItem()))
	                throw new CustomException("EG_WMS_APP_ERR", "ParticularsOfItem is mandatory for creating Work Estimation applications");
	        });
	    	
	    	wmsWorkEstimationRequest.getWmsWorkEstimationApplications().forEach(application -> {
	            if(ObjectUtils.isEmpty(application.getNoS()))
	                throw new CustomException("EG_WMS_APP_ERR", "NoS is mandatory for creating Work Estimation applications");
	        });
	    	
	    	wmsWorkEstimationRequest.getWmsWorkEstimationApplications().forEach(application -> {
	            if(ObjectUtils.isEmpty(application.getCalculationType()))
	                throw new CustomException("EG_WMS_APP_ERR", "CalculationType is mandatory for creating Work Estimation applications");
	        });
	    	
	    	wmsWorkEstimationRequest.getWmsWorkEstimationApplications().forEach(application -> {
	            if(ObjectUtils.isEmpty(application.getAdditionDeduction()))
	                throw new CustomException("EG_WMS_APP_ERR", "AdditionDeduction is mandatory for creating Work Estimation applications");
	        });
	    	
	    	wmsWorkEstimationRequest.getWmsWorkEstimationApplications().forEach(application -> {
	            if(ObjectUtils.isEmpty(application.getLength()))
	                throw new CustomException("EG_WMS_APP_ERR", "Length is mandatory for creating Work Estimation applications");
	        });
	    	
	    	wmsWorkEstimationRequest.getWmsWorkEstimationApplications().forEach(application -> {
	            if(ObjectUtils.isEmpty(application.getLf()))
	                throw new CustomException("EG_WMS_APP_ERR", "Lf is mandatory for creating Work Estimation applications");
	        });
	    	
	    	wmsWorkEstimationRequest.getWmsWorkEstimationApplications().forEach(application -> {
	            if(ObjectUtils.isEmpty(application.getBw()))
	                throw new CustomException("EG_WMS_APP_ERR", "Bw is mandatory for creating Work Estimation applications");
	        });
	    	
	    	wmsWorkEstimationRequest.getWmsWorkEstimationApplications().forEach(application -> {
	            if(ObjectUtils.isEmpty(application.getBwf()))
	                throw new CustomException("EG_WMS_APP_ERR", "Bwf is mandatory for creating Work Estimation applications");
	        });
	    	
	    	wmsWorkEstimationRequest.getWmsWorkEstimationApplications().forEach(application -> {
	            if(ObjectUtils.isEmpty(application.getDh()))
	                throw new CustomException("EG_WMS_APP_ERR", "Dh is mandatory for creating Work Estimation applications");
	        });
	    	
	    	wmsWorkEstimationRequest.getWmsWorkEstimationApplications().forEach(application -> {
	            if(ObjectUtils.isEmpty(application.getDhf()))
	                throw new CustomException("EG_WMS_APP_ERR", "Dhf is mandatory for creating Work Estimation applications");
	        });
	    	
	    	wmsWorkEstimationRequest.getWmsWorkEstimationApplications().forEach(application -> {
	            if(ObjectUtils.isEmpty(application.getQuantity()))
	                throw new CustomException("EG_WMS_APP_ERR", "Quantity is mandatory for creating Work Estimation applications");
	        });
	    	
	    	wmsWorkEstimationRequest.getWmsWorkEstimationApplications().forEach(application -> {
	            if(ObjectUtils.isEmpty(application.getSubTotal()))
	                throw new CustomException("EG_WMS_APP_ERR", "SubTotal is mandatory for creating Work Estimation applications");
	        });
	    	
	    	wmsWorkEstimationRequest.getWmsWorkEstimationApplications().forEach(application -> {
	            if(ObjectUtils.isEmpty(application.getGrandTotal()))
	                throw new CustomException("EG_WMS_APP_ERR", "GrandTotal is mandatory for creating Work Estimation applications");
	        });
	    	
	    	wmsWorkEstimationRequest.getWmsWorkEstimationApplications().forEach(application -> {
	            if(ObjectUtils.isEmpty(application.getEstimatedQuantity()))
	                throw new CustomException("EG_WMS_APP_ERR", "EstimatedQuantity is mandatory for creating Work Estimation applications");
	        });
	    	
	    	wmsWorkEstimationRequest.getWmsWorkEstimationApplications().forEach(application -> {
	            if(ObjectUtils.isEmpty(application.getOverheadCode()))
	                throw new CustomException("EG_WMS_APP_ERR", "OverheadCode is mandatory for creating Work Estimation applications");
	        });
	    	
	    	wmsWorkEstimationRequest.getWmsWorkEstimationApplications().forEach(application -> {
	            if(ObjectUtils.isEmpty(application.getOverheadDescription()))
	                throw new CustomException("EG_WMS_APP_ERR", "OverheadDescription is mandatory for creating Work Estimation applications");
	        });
	    	
	    	wmsWorkEstimationRequest.getWmsWorkEstimationApplications().forEach(application -> {
	            if(ObjectUtils.isEmpty(application.getValueType()))
	                throw new CustomException("EG_WMS_APP_ERR", "ValueType is mandatory for creating Work Estimation applications");
	        });
	    	
	    	
	    }

		public List<WMSWorkEstimationApplication> validateApplicationUpdateRequest(
				WMSWorkEstimationRequest workEstimationRequest) {
			List<String> ids = workEstimationRequest.getWmsWorkEstimationApplications().stream().map(WMSWorkEstimationApplication::getEstimateId).collect(Collectors.toList());
	        List<WMSWorkEstimationApplication> WorkEstimationApplications = repository.getApplications(WMSWorkEstimationApplicationSearchCriteria.builder().estimateId(ids).build());
	        if(WorkEstimationApplications.size() != ids.size())
	            throw new CustomException("APPLICATION_DOES_NOT_EXIST", "One of the Work Estimation ids does not exist.");
	        return WorkEstimationApplications;
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
