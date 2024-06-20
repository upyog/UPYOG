package org.egov.validator;

import java.util.List;
import java.util.stream.Collectors;

import org.egov.repository.WMSBankDetailsRepository;
import org.egov.repository.WMSContractorRepository;
import org.egov.repository.WMSSORRepository;
import org.egov.repository.WMSWorkRepository;
import org.egov.repository.WMSWorkStatusReportRepository;
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
import org.egov.web.models.WMSSORRequest;
import org.egov.web.models.WMSWorkApplication;
import org.egov.web.models.WMSWorkApplicationSearchCriteria;
import org.egov.web.models.WMSWorkRequest;
import org.egov.web.models.WMSWorkStatusReportApplication;
import org.egov.web.models.WMSWorkStatusReportApplicationSearchCriteria;
import org.egov.web.models.WMSWorkStatusReportRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;


@Component
public class WMSWorkStatusReportValidator {
	
	 @Autowired
	    private WMSWorkStatusReportRepository repository;

	    public void validateWorkStatusReportApplication(WMSWorkStatusReportRequest wmsWorkStatusReportRequest) {
	    	wmsWorkStatusReportRequest.getWmsWorkStatusReportApplications().forEach(application -> {
	            if(ObjectUtils.isEmpty(application.getTenantId()))
	                throw new CustomException("EG_WMS_APP_ERR", "tenantId is mandatory for creating WorkStatusReport applications");
	        });
	    }

		public List<WMSWorkStatusReportApplication> validateApplicationUpdateRequest(WMSWorkStatusReportRequest workStatusReportRequest) {
			List<String> ids = workStatusReportRequest.getWmsWorkStatusReportApplications().stream().map(WMSWorkStatusReportApplication::getWsrId).collect(Collectors.toList());
	        List<WMSWorkStatusReportApplication> workStatusReportApplications = repository.getApplications(WMSWorkStatusReportApplicationSearchCriteria.builder().wsrId(ids).build());
	        if(workStatusReportApplications.size() != ids.size())
	            throw new CustomException("APPLICATION_DOES_NOT_EXIST", "One of the WorkStatusReport ids does not exist.");
	        return workStatusReportApplications;
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
