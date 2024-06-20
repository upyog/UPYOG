package org.egov.validator;

import java.util.List;
import java.util.stream.Collectors;

import org.egov.repository.WMSContractorRepository;
import org.egov.repository.WMSRunningAccountFinalBillRepository;
import org.egov.repository.WMSSORRepository;
import org.egov.repository.WMSWorkRepository;
import org.egov.tracer.model.CustomException;
import org.egov.web.models.SORApplicationSearchCriteria;
import org.egov.web.models.ScheduleOfRateApplication;
import org.egov.web.models.WMSContractorApplication;
import org.egov.web.models.WMSContractorApplicationSearchCriteria;
import org.egov.web.models.WMSContractorRequest;
import org.egov.web.models.WMSRunningAccountFinalBillApplication;
import org.egov.web.models.WMSRunningAccountFinalBillApplicationSearchCriteria;
import org.egov.web.models.WMSRunningAccountFinalBillRequest;
import org.egov.web.models.WMSSORRequest;
import org.egov.web.models.WMSWorkApplication;
import org.egov.web.models.WMSWorkApplicationSearchCriteria;
import org.egov.web.models.WMSWorkRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;


@Component
public class WMSRunningAccountFinalBillValidator {
	
	 @Autowired
	    private WMSRunningAccountFinalBillRepository repository;

	    public void validateRunningAccountFinalBillApplication(WMSRunningAccountFinalBillRequest wmsRunningAccountFinalBillRequest) {
	    	wmsRunningAccountFinalBillRequest.getWmsRunningAccountFinalBillApplications().forEach(application -> {
	            if(ObjectUtils.isEmpty(application.getTenantId()))
	                throw new CustomException("EG_WMS_APP_ERR", "tenantId is mandatory for creating Running Account Final Bill applications");
	        });
	    	
	    	wmsRunningAccountFinalBillRequest.getWmsRunningAccountFinalBillApplications().forEach(
	  			  application ->
	  			  application.getPreviousRunningBillInfo().forEach(prun->{if(ObjectUtils.isEmpty(
	  			  prun.getRunningAccountBillAmount())) throw new CustomException("EG_WMS_APP_ERR",
	  			  "running_account_bill_amount is mandatory for creating RunningAccountFinalBill applications");
	  			  }));
	    	
	    	wmsRunningAccountFinalBillRequest.getWmsRunningAccountFinalBillApplications().forEach(
		  			  application ->
		  			  application.getPreviousRunningBillInfo().forEach(prun->{if(ObjectUtils.isEmpty(
		  			  prun.getRunningAccountBillDate())) throw new CustomException("EG_WMS_APP_ERR",
		  			  "running_account_bill_date is mandatory for creating RunningAccountFinalBill applications");
		  			  }));
	    	
	    	wmsRunningAccountFinalBillRequest.getWmsRunningAccountFinalBillApplications().forEach(
		  			  application ->
		  			  application.getPreviousRunningBillInfo().forEach(prun->{if(ObjectUtils.isEmpty(
		  			  prun.getRunningAccountBillNo())) throw new CustomException("EG_WMS_APP_ERR",
		  			  "running_account_bill_no is mandatory for creating RunningAccountFinalBill applications");
		  			  }));
	    	
	    	wmsRunningAccountFinalBillRequest.getWmsRunningAccountFinalBillApplications().forEach(
		  			  application ->
		  			  application.getPreviousRunningBillInfo().forEach(prun->{if(ObjectUtils.isEmpty(
		  			  prun.getTaxAmount())) throw new CustomException("EG_WMS_APP_ERR",
		  			  "tax_Amount is mandatory for creating RunningAccountFinalBill applications");
		  			  }));
	    	
			/*
			 * wmsRunningAccountFinalBillRequest.getWmsRunningAccountFinalBillApplications()
			 * .forEach(application -> { if(ObjectUtils.isEmpty(application.getMbNo()))
			 * throw new CustomException("EG_WMS_APP_ERR",
			 * "MbNo is mandatory for creating Running Account Final Bill applications");
			 * });
			 */
	    	
			/*
			 * wmsRunningAccountFinalBillRequest.getWmsRunningAccountFinalBillApplications()
			 * .forEach(application -> { if(ObjectUtils.isEmpty(application.getMbDate()))
			 * throw new CustomException("EG_WMS_APP_ERR",
			 * "MbDate is mandatory for creating Running Account Final Bill applications");
			 * });
			 */
	    	
			/*
			 * wmsRunningAccountFinalBillRequest.getWmsRunningAccountFinalBillApplications()
			 * .forEach(application -> { if(ObjectUtils.isEmpty(application.getMbAmount()))
			 * throw new CustomException("EG_WMS_APP_ERR",
			 * "MbAmount is mandatory for creating Running Account Final Bill applications"
			 * ); });
			 */
	    	
			/*
			 * wmsRunningAccountFinalBillRequest.getWmsRunningAccountFinalBillApplications()
			 * .forEach(application -> { if(ObjectUtils.isEmpty(application.getWorkName()))
			 * throw new CustomException("EG_WMS_APP_ERR",
			 * "WorkName is mandatory for creating Running Account Final Bill applications"
			 * ); });
			 */
	    	
			/*
			 * wmsRunningAccountFinalBillRequest.getWmsRunningAccountFinalBillApplications()
			 * .forEach(application -> {
			 * if(ObjectUtils.isEmpty(application.getEstimatedCost())) throw new
			 * CustomException("EG_WMS_APP_ERR",
			 * "EstimatedCost is mandatory for creating Running Account Final Bill applications"
			 * ); });
			 */
	    	
			/*
			 * wmsRunningAccountFinalBillRequest.getWmsRunningAccountFinalBillApplications()
			 * .forEach(application -> {
			 * if(ObjectUtils.isEmpty(application.getTenderType())) throw new
			 * CustomException("EG_WMS_APP_ERR",
			 * "TenderType is mandatory for creating Running Account Final Bill applications"
			 * ); });
			 */
	    	
			/*
			 * wmsRunningAccountFinalBillRequest.getWmsRunningAccountFinalBillApplications()
			 * .forEach(application -> {
			 * if(ObjectUtils.isEmpty(application.getAwardAmount())) throw new
			 * CustomException("EG_WMS_APP_ERR",
			 * "AwardAmount is mandatory for creating Running Account Final Bill applications"
			 * ); });
			 */
	    	
			/*
			 * wmsRunningAccountFinalBillRequest.getWmsRunningAccountFinalBillApplications()
			 * .forEach(application -> { if(ObjectUtils.isEmpty(application.getSrNo()))
			 * throw new CustomException("EG_WMS_APP_ERR",
			 * "SrNo is mandatory for creating Running Account Final Bill applications");
			 * });
			 */
	    	
			/*
			 * wmsRunningAccountFinalBillRequest.getWmsRunningAccountFinalBillApplications()
			 * .forEach(application -> {
			 * if(ObjectUtils.isEmpty(application.getDeductionDescription())) throw new
			 * CustomException("EG_WMS_APP_ERR",
			 * "DeductionDescription is mandatory for creating Running Account Final Bill applications"
			 * ); });
			 */
	    	
			/*
			 * wmsRunningAccountFinalBillRequest.getWmsRunningAccountFinalBillApplications()
			 * .forEach(application -> {
			 * if(ObjectUtils.isEmpty(application.getAdditionDeduction())) throw new
			 * CustomException("EG_WMS_APP_ERR",
			 * "AdditionDeduction is mandatory for creating Running Account Final Bill applications"
			 * ); });
			 */
	    	
			/*
			 * wmsRunningAccountFinalBillRequest.getWmsRunningAccountFinalBillApplications()
			 * .forEach(application -> {
			 * if(ObjectUtils.isEmpty(application.getCalculationMethod())) throw new
			 * CustomException("EG_WMS_APP_ERR",
			 * "CalculationMethod is mandatory for creating Running Account Final Bill applications"
			 * ); });
			 */
	    	
			/*
			 * wmsRunningAccountFinalBillRequest.getWmsRunningAccountFinalBillApplications()
			 * .forEach(application -> {
			 * if(ObjectUtils.isEmpty(application.getDeductionAmount())) throw new
			 * CustomException("EG_WMS_APP_ERR",
			 * "DeductionAmount is mandatory for creating Running Account Final Bill applications"
			 * ); });
			 */
	    	
			/*
			 * wmsRunningAccountFinalBillRequest.getWmsRunningAccountFinalBillApplications()
			 * .forEach(application -> {
			 * if(ObjectUtils.isEmpty(application.getWorkOrderNo())) throw new
			 * CustomException("EG_WMS_APP_ERR",
			 * "WorkOrderNo is mandatory for creating Running Account Final Bill applications"
			 * ); });
			 */
	    }

		public List<WMSRunningAccountFinalBillApplication> validateApplicationUpdateRequest(
				WMSRunningAccountFinalBillRequest runningAccountFinalBillRequest) {
			List<String> ids = runningAccountFinalBillRequest.getWmsRunningAccountFinalBillApplications().stream().map(WMSRunningAccountFinalBillApplication::getRunningAccountId).collect(Collectors.toList());
	        List<WMSRunningAccountFinalBillApplication> runningAccountFinalBillApplications = repository.getApplications(WMSRunningAccountFinalBillApplicationSearchCriteria.builder().runningAccountId(ids).build());
	        if(runningAccountFinalBillApplications.size() != ids.size())
	            throw new CustomException("APPLICATION_DOES_NOT_EXIST", "One of the Running Account Final Bill ids does not exist.");
	        return runningAccountFinalBillApplications;
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
