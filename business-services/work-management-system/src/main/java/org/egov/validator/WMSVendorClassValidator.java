package org.egov.validator;

import java.util.List;
import java.util.stream.Collectors;

import org.egov.repository.WMSBankDetailsRepository;
import org.egov.repository.WMSContractorRepository;
import org.egov.repository.WMSContractorSubTypeRepository;
import org.egov.repository.WMSSORRepository;
import org.egov.repository.WMSVendorClassRepository;
import org.egov.repository.WMSVendorTypeRepository;
import org.egov.repository.WMSWorkRepository;
import org.egov.tracer.model.CustomException;
import org.egov.web.models.SORApplicationSearchCriteria;
import org.egov.web.models.ScheduleOfRateApplication;
import org.egov.web.models.Scheme;
import org.egov.web.models.SchemeApplicationSearchCriteria;
import org.egov.web.models.WMSBankDetailsApplication;
import org.egov.web.models.WMSBankDetailsApplicationSearchCriteria;
import org.egov.web.models.WMSBankDetailsRequest;
import org.egov.web.models.WMSContractorApplication;
import org.egov.web.models.WMSContractorApplicationSearchCriteria;
import org.egov.web.models.WMSContractorRequest;
import org.egov.web.models.WMSContractorSubTypeApplication;
import org.egov.web.models.WMSContractorSubTypeApplicationSearchCriteria;
import org.egov.web.models.WMSContractorSubTypeRequest;
import org.egov.web.models.WMSSORRequest;
import org.egov.web.models.WMSVendorClassApplication;
import org.egov.web.models.WMSVendorClassApplicationSearchCriteria;
import org.egov.web.models.WMSVendorClassRequest;
import org.egov.web.models.WMSVendorTypeApplication;
import org.egov.web.models.WMSVendorTypeApplicationSearchCriteria;
import org.egov.web.models.WMSVendorTypeRequest;
import org.egov.web.models.WMSWorkApplication;
import org.egov.web.models.WMSWorkApplicationSearchCriteria;
import org.egov.web.models.WMSWorkRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;


@Component
public class WMSVendorClassValidator {
	
	 @Autowired
	    private WMSVendorClassRepository repository;

	    public void validateVendorClassApplication(WMSVendorClassRequest wmsVendorClassRequest) {
	    	wmsVendorClassRequest.getWmsVendorClassApplications().forEach(application -> {
	            if(ObjectUtils.isEmpty(application.getTenantId()))
	                throw new CustomException("EG_WMS_APP_ERR", "tenantId is mandatory for creating VendorClass applications");
	        });
	    }

		public List<WMSVendorClassApplication> validateApplicationUpdateRequest(WMSVendorClassRequest vendorClassRequest) {
			List<String> ids = vendorClassRequest.getWmsVendorClassApplications().stream().map(WMSVendorClassApplication::getVendorClassId).collect(Collectors.toList());
	        List<WMSVendorClassApplication> vendorClassApplications = repository.getApplications(WMSVendorClassApplicationSearchCriteria.builder().vendorClassId(ids).build());
	        if(vendorClassApplications.size() != ids.size())
	            throw new CustomException("APPLICATION_DOES_NOT_EXIST", "One of the VendorClass ids does not exist.");
	        return vendorClassApplications;
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
