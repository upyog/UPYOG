package org.upyog.sv.service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.egov.common.contract.request.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.upyog.sv.config.StreetVendingConfiguration;
import org.upyog.sv.constants.StreetVendingConstants;
import org.upyog.sv.repository.DemandRepository;
import org.upyog.sv.util.StreetVendingUtil;
import org.upyog.sv.web.models.StreetVendingRequest;
import org.upyog.sv.web.models.VendorDetail;
import org.upyog.sv.web.models.billing.Demand;
import org.upyog.sv.web.models.billing.DemandDetail;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DemandService {

	@Autowired
	private StreetVendingConfiguration config;

	@Autowired
	private CalculationService calculationService;

	@Autowired
	private DemandRepository demandRepository;

	/**
	 * 1. Fetch tax heads from mdms tax-heads.json 2. Map amount to tax heads from
	 * CalculateType.json 3. Create XDemand for particular tax heads 4. Bill will be
	 * automatically generated when fetch bill api is called for demand created by
	 * this API
	 * 
	 * @param streetVendingRequest, mdmsData
	 * @return List<Demand>
	 */

	public List<Demand> createDemand(StreetVendingRequest streetVendingRequest, Object mdmsData) {
		String tenantId = streetVendingRequest.getStreetVendingDetail().getTenantId();
		String consumerCode = streetVendingRequest.getStreetVendingDetail().getApplicationNo();
				
		VendorDetail vendorDetail = null;
		for (VendorDetail detail : streetVendingRequest.getStreetVendingDetail().getVendorDetail()) {
		    if (StreetVendingConstants.VENDOR.equals(detail.getRelationshipType())) {
		        vendorDetail = detail;
		        break; 
		    }
		}
		
		User user = User.builder().name(vendorDetail.getName()).emailId(vendorDetail.getEmailId())
				.mobileNumber(vendorDetail.getMobileNo()).tenantId(streetVendingRequest.getStreetVendingDetail().getTenantId()).build();

		List<DemandDetail> demandDetails = calculationService.calculateDemand(streetVendingRequest);
		
		log.info("demandDetails : " + demandDetails);
		
		String businessService = config.getModuleName(); 
		long taxPeriodFrom = StreetVendingUtil.getCurrentTimestamp();
		long taxPeriodTo = taxPeriodFrom + Duration.ofDays(365).toMillis();;
		
		// If demand details are present, determine the business service based on tax head code
		if (demandDetails != null && !demandDetails.isEmpty()) {
			
			// Get the tax head code from demand detail entry
		    String taxHeadCode = demandDetails.get(0).getTaxHeadMasterCode();
		    
		    // Check if it's a monthly tax head and override the business service accordingly
		    if (StreetVendingConstants.TAXHEADMONTHLY.equals(taxHeadCode)) {
		    	businessService = config.getServiceNameMonthly();
		    	taxPeriodTo = taxPeriodFrom + Duration.ofDays(30).toMillis();
		    // Check if it's a quarterly tax head and override the business service accordingly
		    } else if (StreetVendingConstants.TAXHEADQUATERLY.equals(taxHeadCode)) {
		    	businessService = config.getServiceNameQuaterly();
		    	taxPeriodTo = taxPeriodFrom + Duration.ofDays(90).toMillis();
		    } 
		}
		// TODO: change from date and to date from MDMS
		Demand demand = Demand.builder().consumerCode(consumerCode).demandDetails(demandDetails).payer(user)
				.tenantId(tenantId).taxPeriodFrom(taxPeriodFrom).taxPeriodTo(taxPeriodTo)
				.consumerType(config.getModuleName()).businessService(businessService).additionalDetails(null)
				.build();

		List<Demand> demands = new ArrayList<>();
		demands.add(demand);

		log.info(" SV Sending call to billing service for generating demand for application no : " + consumerCode);
		return demandRepository.saveDemand(streetVendingRequest.getRequestInfo(), demands);
	}

}
