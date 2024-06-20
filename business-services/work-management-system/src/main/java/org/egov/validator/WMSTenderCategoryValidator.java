package org.egov.validator;

import java.util.List;
import java.util.stream.Collectors;

import org.egov.repository.WMSBankDetailsRepository;
import org.egov.repository.WMSContractorRepository;
import org.egov.repository.WMSDepartmentRepository;
import org.egov.repository.WMSSORRepository;
import org.egov.repository.WMSTenderCategoryRepository;
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
import org.egov.web.models.WMSDepartmentApplication;
import org.egov.web.models.WMSDepartmentApplicationSearchCriteria;
import org.egov.web.models.WMSDepartmentRequest;
import org.egov.web.models.WMSSORRequest;
import org.egov.web.models.WMSTenderCategoryApplication;
import org.egov.web.models.WMSTenderCategoryApplicationSearchCriteria;
import org.egov.web.models.WMSTenderCategoryRequest;
import org.egov.web.models.WMSWorkApplication;
import org.egov.web.models.WMSWorkApplicationSearchCriteria;
import org.egov.web.models.WMSWorkRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;


@Component
public class WMSTenderCategoryValidator {
	
	 @Autowired
	    private WMSTenderCategoryRepository repository;

	    public void validateTenderCategoryApplication(WMSTenderCategoryRequest wmsTenderCategoryRequest) {
	    	wmsTenderCategoryRequest.getWmsTenderCategoryApplications().forEach(application -> {
	            if(ObjectUtils.isEmpty(application.getTenantId()))
	                throw new CustomException("EG_WMS_APP_ERR", "tenantId is mandatory for creating TenderCategory applications");
	        });
	    }

		public List<WMSTenderCategoryApplication> validateApplicationUpdateRequest(WMSTenderCategoryRequest tenderCategoryRequest) {
			List<String> ids = tenderCategoryRequest.getWmsTenderCategoryApplications().stream().map(WMSTenderCategoryApplication::getCategoryId).collect(Collectors.toList());
	        List<WMSTenderCategoryApplication> tenderCategoryApplications = repository.getApplications(WMSTenderCategoryApplicationSearchCriteria.builder().categoryId(ids).build());
	        if(tenderCategoryApplications.size() != ids.size())
	            throw new CustomException("APPLICATION_DOES_NOT_EXIST", "One of the TenderCategory ids does not exist.");
	        return tenderCategoryApplications;
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
