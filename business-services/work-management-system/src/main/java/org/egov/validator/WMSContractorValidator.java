package org.egov.validator;

import java.util.List;
import java.util.stream.Collectors;

import org.egov.repository.WMSContractorRepository;
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
import org.egov.web.models.WMSSORRequest;
import org.egov.web.models.WMSWorkApplication;
import org.egov.web.models.WMSWorkApplicationSearchCriteria;
import org.egov.web.models.WMSWorkRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;


@Component
public class WMSContractorValidator {
	
	 @Autowired
	    private WMSContractorRepository repository;

	    public void validateContractorApplication(WMSContractorRequest wmsContractorRequest) {
	    	wmsContractorRequest.getWmsContractorApplications().forEach(application -> {
	            if(ObjectUtils.isEmpty(application.getTenantId()))
	                throw new CustomException("EG_WMS_APP_ERR", "tenantId is mandatory for creating Contractor applications");
	        });
	    	
	    	wmsContractorRequest.getWmsContractorApplications().forEach(application -> {
	            if(ObjectUtils.isEmpty(application.getVendorType()))
	                throw new CustomException("EG_WMS_APP_ERR", "Vendor Type is mandatory for creating Contractor applications");
	        });
	    	wmsContractorRequest.getWmsContractorApplications().forEach(application -> {
	            if(ObjectUtils.isEmpty(application.getVendorSubType()))
	                throw new CustomException("EG_WMS_APP_ERR", "Vendor SubType is mandatory for creating Contractor applications");
	        });
	    	wmsContractorRequest.getWmsContractorApplications().forEach(application -> {
	            if(ObjectUtils.isEmpty(application.getVendorName()))
	                throw new CustomException("EG_WMS_APP_ERR", "Vendor Name is mandatory for creating Contractor applications");
	        });
	    	wmsContractorRequest.getWmsContractorApplications().forEach(application -> {
	            if(ObjectUtils.isEmpty(application.getVendorClass()))
	                throw new CustomException("EG_WMS_APP_ERR", "Vendor Class is mandatory for creating Contractor applications");
	        });
	    	
	    	wmsContractorRequest.getWmsContractorApplications().forEach(application -> {
	            if(ObjectUtils.isEmpty(application.getAddress()))
	                throw new CustomException("EG_WMS_APP_ERR", "Address is mandatory for creating Contractor applications");
	        });
	    }

		public List<WMSContractorApplication> validateApplicationUpdateRequest(WMSContractorRequest contractorRequest) {
			List<Integer> ids = contractorRequest.getWmsContractorApplications().stream().map(WMSContractorApplication::getVendorId).collect(Collectors.toList());
	        List<WMSContractorApplication> contractorApplications = repository.getApplications(WMSContractorApplicationSearchCriteria.builder().vendorId(ids).build());
	        if(contractorApplications.size() != ids.size())
	            throw new CustomException("APPLICATION_DOES_NOT_EXIST", "One of the Contractor ids does not exist.");
	        return contractorApplications;
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
