package org.upyog.sv.service;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.upyog.sv.constants.StreetVendingConstants;
import org.upyog.sv.util.MdmsUtil;
import org.upyog.sv.web.models.StreetVendingRequest;
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

		List<DemandDetail> demandDetails = processCalculationForDemandGeneration(tenantId, calculationTypes,
				bookingRequest, headMasters);

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

}
