package org.upyog.sv.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.upyog.sv.config.StreetVendingConfiguration;
import org.upyog.sv.repository.DemandRepository;
import org.upyog.sv.util.StreetVendingUtil;
import org.upyog.sv.web.models.StreetVendingRequest;
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
	 * @param bookingRequest
	 * @return
	 */

	public List<Demand> createDemand(StreetVendingRequest streetVendingRequest, Object mdmsData) {
		String tenantId = streetVendingRequest.getStreetVendingDetail().getTenantId();
		String consumerCode = streetVendingRequest.getStreetVendingDetail().getApplicationNo();

		org.egov.common.contract.request.User user = streetVendingRequest.getRequestInfo().getUserInfo();

		List<DemandDetail> demandDetails = calculationService.calculateDemand(streetVendingRequest);

		// TODO: change from date and to date from MDMS
		Demand demand = Demand.builder().consumerCode(consumerCode).demandDetails(demandDetails).payer(user)
				.tenantId(tenantId).taxPeriodFrom(StreetVendingUtil.getCurrentTimestamp()).taxPeriodTo(1869676199000l)
				.consumerType(config.getModuleName()).businessService(config.getModuleName()).additionalDetails(null)
				.build();

		List<Demand> demands = new ArrayList<>();
		demands.add(demand);

		log.info(" SV Sending call to billing service for generating demand for application no : " + consumerCode);
		return demandRepository.saveDemand(streetVendingRequest.getRequestInfo(), demands);
	}

}
