package org.upyog.sv.service;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.upyog.sv.constants.StreetVendingConstants;
import org.upyog.sv.util.MdmsUtil;
import org.upyog.sv.web.models.StreetVendingDetail;
import org.upyog.sv.web.models.StreetVendingRequest;
import org.upyog.sv.web.models.Workflow;
import org.upyog.sv.web.models.billing.CalculationType;
import org.upyog.sv.web.models.billing.DemandDetail;
import org.upyog.sv.web.models.billing.TaxHeadMaster;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CalculationService {

	@Autowired
	private MdmsUtil mdmsUtil;

	/**
	 * @param bookingRequest
	 * @return
	 */
	public List<DemandDetail> calculateDemand(StreetVendingRequest bookingRequest) {

		String tenantId = bookingRequest.getStreetVendingDetail().getTenantId().split("\\.")[0];

		List<TaxHeadMaster> headMasters = mdmsUtil.getTaxHeadMasterList(bookingRequest.getRequestInfo(), tenantId,
				StreetVendingConstants.BILLING_SERVICE);

		List<CalculationType> calculationTypes = mdmsUtil.getcalculationType(bookingRequest.getRequestInfo(), tenantId,
				StreetVendingConstants.SV_MASTER_MODULE_NAME);

		log.info("calculationTypes " + calculationTypes);
		
		// Get the vendor's payment frequency (e.g., MONTHLY, QUARTERLY) from the booking request	
	    StreetVendingDetail svDetail = bookingRequest.getStreetVendingDetail();
	    Workflow workflow = (svDetail != null) ? svDetail.getWorkflow() : null;

	    String action = (workflow != null && workflow.getAction() != null) ? workflow.getAction() : "";

	    String vendorPaymentFrequency = (svDetail != null) ? svDetail.getVendorPaymentFrequency() : null;

	    String requiredApplicationType;

	    if (StreetVendingConstants.ACTION_APPROVE.equalsIgnoreCase(action)) {
	        requiredApplicationType = StreetVendingConstants.SVONETIMEFEE;
	    } else {
	        requiredApplicationType = getApplicationTypeFromFrequency(vendorPaymentFrequency);
	    }
      
	    //Filter the list of calculation types to include only those matching the derived application type
	    List<CalculationType> filteredCalculationType = calculationTypes.stream()
	            .filter(type -> requiredApplicationType.equalsIgnoreCase(type.getApplicationType()))
	            .collect(Collectors.toList());

		List<DemandDetail> demandDetails = processCalculationForDemandGeneration(tenantId, filteredCalculationType,
				bookingRequest, headMasters);
		
		log.info("demandDetails : " + demandDetails);
		
		log.info("Demand Amount : " + demandDetails.get(0).getTaxAmount());
	    
		return demandDetails;

	}

	private List<DemandDetail> processCalculationForDemandGeneration(String tenantId,
			List<CalculationType> calculationTypes, StreetVendingRequest vendingRequest,
			List<TaxHeadMaster> headMasters) {

		List<DemandDetail> demandDetails = new LinkedList<>();

		List<String> taxHeadMasters = headMasters.stream().map(head -> head.getCode()).collect(Collectors.toList());

		log.info("tax head masters  : " + taxHeadMasters);

		for (CalculationType type : calculationTypes) {
			if (!taxHeadMasters.contains(type.getFeeType())) {
				throw new CustomException("TAX_HEAD_MASTER_INVALID", "Tax Header Master not found for " + type);
			}
		}

		for (CalculationType type : calculationTypes) {
			if (taxHeadMasters.contains(type.getFeeType())) {
				DemandDetail demandDetail = DemandDetail.builder().taxAmount(type.getAmount())
						.taxHeadMasterCode(type.getFeeType()).tenantId(tenantId).build();
				demandDetails.add(demandDetail);
			}
		}
		return demandDetails;

	}
	
	/**
	 * Determines the application type based on the vendor's payment frequency.
	 *
	 * @param vendorPaymentfrequency the payment frequency provided by the vendor (e.g., "MONTHLY", "QUARTERLY")
	 * @return the corresponding application type constant (e.g., "SV_MONTHLY_FEE" or "SV_QUARTERLY_FEE")
	 *         Defaults to quarterly fee type if the frequency is unrecognized or null.
	 */
	
	private String getApplicationTypeFromFrequency(String vendorPaymentfrequency) {
	    if (StreetVendingConstants.MONTHLY.equalsIgnoreCase(vendorPaymentfrequency)) {
	        return StreetVendingConstants.SVMONTHLYFEE;
	    } else if (StreetVendingConstants.QUATERLY.equalsIgnoreCase(vendorPaymentfrequency)) {
	        return  StreetVendingConstants.SVQUATERLYYFEE;
	    } else {
	        return StreetVendingConstants.SVONETIMEFEE;
	    }
	}


}
