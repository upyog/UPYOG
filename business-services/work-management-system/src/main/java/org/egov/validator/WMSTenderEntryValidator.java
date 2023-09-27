package org.egov.validator;

import java.util.List;
import java.util.stream.Collectors;

import org.egov.repository.WMSContractorRepository;
import org.egov.repository.WMSSORRepository;
import org.egov.repository.WMSTenderEntryRepository;
import org.egov.repository.WMSWorkRepository;
import org.egov.tracer.model.CustomException;
import org.egov.web.models.SORApplicationSearchCriteria;
import org.egov.web.models.ScheduleOfRateApplication;
import org.egov.web.models.WMSContractorApplication;
import org.egov.web.models.WMSContractorApplicationSearchCriteria;
import org.egov.web.models.WMSContractorRequest;
import org.egov.web.models.WMSSORRequest;
import org.egov.web.models.WMSTenderEntryApplication;
import org.egov.web.models.WMSTenderEntryApplicationSearchCriteria;
import org.egov.web.models.WMSTenderEntryRequest;
import org.egov.web.models.WMSWorkApplication;
import org.egov.web.models.WMSWorkApplicationSearchCriteria;
import org.egov.web.models.WMSWorkRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;


@Component
public class WMSTenderEntryValidator {
	
	 @Autowired
	    private WMSTenderEntryRepository repository;

	    public void validateTenderEntryApplication(WMSTenderEntryRequest wmsTenderEntryRequest) {
	    	wmsTenderEntryRequest.getWmsTenderEntryApplications().forEach(application -> {
	            if(ObjectUtils.isEmpty(application.getTenantId()))
	                throw new CustomException("EG_WMS_APP_ERR", "tenantId is mandatory for creating Tender Entry applications");
	        });
	    	
	    	wmsTenderEntryRequest.getWmsTenderEntryApplications().forEach(application -> {
	            if(ObjectUtils.isEmpty(application.getDepartmentName()))
	                throw new CustomException("EG_WMS_APP_ERR", "DepartmentName is mandatory for creating Tender Entry applications");
	        });
	    	
	    	wmsTenderEntryRequest.getWmsTenderEntryApplications().forEach(application -> {
	            if(ObjectUtils.isEmpty(application.getRequestCategory()))
	                throw new CustomException("EG_WMS_APP_ERR", "RequestCategory is mandatory for creating Tender Entry applications");
	        });
	    	
	    	wmsTenderEntryRequest.getWmsTenderEntryApplications().forEach(application -> {
	            if(ObjectUtils.isEmpty(application.getProjectName()))
	                throw new CustomException("EG_WMS_APP_ERR", "ProjectName is mandatory for creating Tender Entry applications");
	        });
	    	
	    	wmsTenderEntryRequest.getWmsTenderEntryApplications().forEach(application -> {
	            if(ObjectUtils.isEmpty(application.getPrebidMeetingDate()))
	                throw new CustomException("EG_WMS_APP_ERR", "PrebidMeetingDate is mandatory for creating Tender Entry applications");
	        });
	    	
	    	wmsTenderEntryRequest.getWmsTenderEntryApplications().forEach(application -> {
	            if(ObjectUtils.isEmpty(application.getPrebidMeetingLocation()))
	                throw new CustomException("EG_WMS_APP_ERR", "PrebidMeetingLocation is mandatory for creating Tender Entry applications");
	        });
	    	
	    	wmsTenderEntryRequest.getWmsTenderEntryApplications().forEach(application -> {
	            if(ObjectUtils.isEmpty(application.getIssueFromDate()))
	                throw new CustomException("EG_WMS_APP_ERR", "IssueFromDate is mandatory for creating Tender Entry applications");
	        });
	    	
	    	wmsTenderEntryRequest.getWmsTenderEntryApplications().forEach(application -> {
	            if(ObjectUtils.isEmpty(application.getIssueTillDate()))
	                throw new CustomException("EG_WMS_APP_ERR", "IssueTillDate is mandatory for creating Tender Entry applications");
	        });
	    	
	    	wmsTenderEntryRequest.getWmsTenderEntryApplications().forEach(application -> {
	            if(ObjectUtils.isEmpty(application.getPublishDate()))
	                throw new CustomException("EG_WMS_APP_ERR", "PublishDate is mandatory for creating Tender Entry applications");
	        });
	    	
	    	wmsTenderEntryRequest.getWmsTenderEntryApplications().forEach(application -> {
	            if(ObjectUtils.isEmpty(application.getTechnicalBidOpenDate()))
	                throw new CustomException("EG_WMS_APP_ERR", "TechnicalBidOpenDate is mandatory for creating Tender Entry applications");
	        });
	    	
	    	wmsTenderEntryRequest.getWmsTenderEntryApplications().forEach(application -> {
	            if(ObjectUtils.isEmpty(application.getFinancialBidOpenDate()))
	                throw new CustomException("EG_WMS_APP_ERR", "FinancialBidOpenDate is mandatory for creating Tender Entry applications");
	        });
	    	
	    	wmsTenderEntryRequest.getWmsTenderEntryApplications().forEach(application -> {
	            if(ObjectUtils.isEmpty(application.getValidity()))
	                throw new CustomException("EG_WMS_APP_ERR", "Validity is mandatory for creating Tender Entry applications");
	        });
	    	
			/*
			 * wmsTenderEntryRequest.getWmsTenderEntryApplications().forEach(application ->
			 * { if(ObjectUtils.isEmpty(application.getUploadDocument())) throw new
			 * CustomException("EG_WMS_APP_ERR",
			 * "UploadDocument is mandatory for creating Tender Entry applications"); });
			 */
			/*
			 * wmsTenderEntryRequest.getWmsTenderEntryApplications().forEach(application ->
			 * { if(ObjectUtils.isEmpty(application.getWorkNo())) throw new
			 * CustomException("EG_WMS_APP_ERR",
			 * "WorkNo is mandatory for creating Tender Entry applications"); });
			 * 
			 * wmsTenderEntryRequest.getWmsTenderEntryApplications().forEach(application ->
			 * { if(ObjectUtils.isEmpty(application.getWorkDescription())) throw new
			 * CustomException("EG_WMS_APP_ERR",
			 * "WorkDescription is mandatory for creating Tender Entry applications"); });
			 * 
			 * wmsTenderEntryRequest.getWmsTenderEntryApplications().forEach(application ->
			 * { if(ObjectUtils.isEmpty(application.getEstimatedCost())) throw new
			 * CustomException("EG_WMS_APP_ERR",
			 * "EstimatedCost is mandatory for creating Tender Entry applications"); });
			 * 
			 * wmsTenderEntryRequest.getWmsTenderEntryApplications().forEach(application ->
			 * { if(ObjectUtils.isEmpty(application.getTenderType())) throw new
			 * CustomException("EG_WMS_APP_ERR",
			 * "TenderType is mandatory for creating Tender Entry applications"); });
			 * 
			 * wmsTenderEntryRequest.getWmsTenderEntryApplications().forEach(application ->
			 * { if(ObjectUtils.isEmpty(application.getTenderFee())) throw new
			 * CustomException("EG_WMS_APP_ERR",
			 * "TenderFee is mandatory for creating Tender Entry applications"); });
			 * 
			 * wmsTenderEntryRequest.getWmsTenderEntryApplications().forEach(application ->
			 * { if(ObjectUtils.isEmpty(application.getEmd())) throw new
			 * CustomException("EG_WMS_APP_ERR",
			 * "Emd is mandatory for creating Tender Entry applications"); });
			 * 
			 * wmsTenderEntryRequest.getWmsTenderEntryApplications().forEach(application ->
			 * { if(ObjectUtils.isEmpty(application.getVendorClass())) throw new
			 * CustomException("EG_WMS_APP_ERR",
			 * "VendorClass is mandatory for creating Tender Entry applications"); });
			 * 
			 * wmsTenderEntryRequest.getWmsTenderEntryApplications().forEach(application ->
			 * { if(ObjectUtils.isEmpty(application.getWorkDuration())) throw new
			 * CustomException("EG_WMS_APP_ERR",
			 * "WorkDuration is mandatory for creating Tender Entry applications"); });
			 */
	    }

		public List<WMSTenderEntryApplication> validateApplicationUpdateRequest(
				WMSTenderEntryRequest tenderEntryRequest) {
			List<String> ids = tenderEntryRequest.getWmsTenderEntryApplications().stream().map(WMSTenderEntryApplication::getTenderId).collect(Collectors.toList());
	        List<WMSTenderEntryApplication> tenderEntryApplications = repository.getApplications(WMSTenderEntryApplicationSearchCriteria.builder().tenderId(ids).build());
	        if(tenderEntryApplications.size() != ids.size())
	            throw new CustomException("APPLICATION_DOES_NOT_EXIST", "One of the Tender Entry ids does not exist.");
	        return tenderEntryApplications;
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
