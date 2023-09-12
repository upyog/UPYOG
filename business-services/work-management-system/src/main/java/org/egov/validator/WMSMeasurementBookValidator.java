package org.egov.validator;

import java.util.List;
import java.util.stream.Collectors;

import org.egov.repository.WMSContractorRepository;
import org.egov.repository.WMSMeasurementBookRepository;
import org.egov.repository.WMSSORRepository;
import org.egov.repository.WMSWorkRepository;
import org.egov.tracer.model.CustomException;
import org.egov.web.models.SORApplicationSearchCriteria;
import org.egov.web.models.ScheduleOfRateApplication;
import org.egov.web.models.WMSContractorApplication;
import org.egov.web.models.WMSContractorApplicationSearchCriteria;
import org.egov.web.models.WMSContractorRequest;
import org.egov.web.models.WMSMeasurementBookApplication;
import org.egov.web.models.WMSMeasurementBookApplicationSearchCriteria;
import org.egov.web.models.WMSMeasurementBookRequest;
import org.egov.web.models.WMSSORRequest;
import org.egov.web.models.WMSWorkApplication;
import org.egov.web.models.WMSWorkApplicationSearchCriteria;
import org.egov.web.models.WMSWorkRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;


@Component
public class WMSMeasurementBookValidator {
	
	 @Autowired
	    private WMSMeasurementBookRepository repository;

	    public void validateMeasurementBookApplication(WMSMeasurementBookRequest wmsMeasurementBookRequest) {
	    	wmsMeasurementBookRequest.getWmsMeasurementBookApplications().forEach(application -> {
	            if(ObjectUtils.isEmpty(application.getTenantId()))
	                throw new CustomException("EG_WMS_APP_ERR", "tenantId is mandatory for creating Measurement Book applications");
	        });
	    }

		public List<WMSMeasurementBookApplication> validateApplicationUpdateRequest(
				WMSMeasurementBookRequest measurementBookRequest) {
			List<String> ids = measurementBookRequest.getWmsMeasurementBookApplications().stream().map(WMSMeasurementBookApplication::getMeasurementBookId).collect(Collectors.toList());
	        List<WMSMeasurementBookApplication> contractorApplications = repository.getApplications(WMSMeasurementBookApplicationSearchCriteria.builder().measurementBookId(ids).build());
	        if(contractorApplications.size() != ids.size())
	            throw new CustomException("APPLICATION_DOES_NOT_EXIST", "One of the Measurement Book ids does not exist.");
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
