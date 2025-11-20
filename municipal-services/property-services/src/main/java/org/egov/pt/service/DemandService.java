package org.egov.pt.service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.User;
import org.egov.pt.models.bill.Demand.StatusEnum;

import org.egov.pt.models.CalculateTaxRequest;
import org.egov.pt.models.Property;
import org.egov.pt.models.bill.Demand;
import org.egov.pt.models.bill.DemandDetail;
import org.egov.pt.models.bill.DemandResponse;
import org.egov.pt.repository.DemandRepository;
import org.egov.pt.util.PTConstants;
import org.egov.pt.web.contracts.RequestInfoWrapper;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DemandService {

	@Autowired
	private DemandRepository demandRepository;

	public List<Demand> generateDemand(CalculateTaxRequest calculateTaxRequest, Property property,
			String businessService, BigDecimal taxAmount) {

		DemandDetail demandDetail = DemandDetail.builder().taxHeadMasterCode(PTConstants.PROPERTY_TAX_HEAD_MASTER_CODE)
				.taxAmount(taxAmount).collectionAmount(BigDecimal.ZERO).build();
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, Integer.valueOf(365));
		cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE), 23, 59, 59);

		ObjectMapper mapper = new ObjectMapper();
		ObjectNode node = mapper.createObjectNode();

		JsonNode addDetail = mapper.valueToTree(property.getAddress().getAdditionalDetails());

		String wardName = null;
		if (addDetail != null && addDetail.has("wardNumber")) {
			wardName = addDetail.get("wardNumber").asText();
		}
		node.put("ward", StringUtils.isNotEmpty(wardName) ? wardName : "N/A");
		node.put("oldPropertyId",
				StringUtils.isNotEmpty(property.getOldPropertyId()) ? property.getOldPropertyId() : "N/A");
		node.put("ownerOldCustomerId",
				StringUtils.isNotEmpty(
						property.getOwners().get(0).getAdditionalDetails().get("ownerOldCustomerId").asText())
								? property.getOwners().get(0).getAdditionalDetails().get("ownerOldCustomerId").asText()
								: "N/A");
		node.put("ownerName",
				StringUtils.isNotEmpty(property.getOwners().get(0).getPropertyOwnerName())
						? property.getOwners().get(0).getPropertyOwnerName()
						: "N/A");
		node.put("contactNumber",
				StringUtils.isNotEmpty(property.getOwners().get(0).getMobileNumber())
						? property.getOwners().get(0).getMobileNumber()
						: "N/A");

		Demand demandOne = Demand.builder().consumerCode(property.getPropertyId())
				.demandDetails(Arrays.asList(demandDetail)).minimumAmountPayable(taxAmount)
				.payer(User.builder().uuid(property.getOwners().get(0).getUuid()).build())
				.tenantId(property.getTenantId()).taxPeriodFrom(calculateTaxRequest.getFromDate().getTime())
				.taxPeriodTo(calculateTaxRequest.getToDate().getTime()).fixedBillExpiryDate(cal.getTimeInMillis())
				.additionalDetails(node).consumerType(PTConstants.MODULE_PROPERTY)
				.businessService(PTConstants.MODULE_PROPERTY).build();

		List<Demand> demands = Arrays.asList(demandOne);

		List<Demand> savedDemands = demandRepository.saveDemand(calculateTaxRequest.getRequestInfo(), demands);

		return savedDemands;
	}

	List<Demand> searchDemand(String tenantId, Set<String> demandIds, Set<String> consumerCodes,
			RequestInfo requestInfo, String businessService) {

		RequestInfoWrapper requestInfoWrapper = RequestInfoWrapper.builder().requestInfo(requestInfo).build();
		DemandResponse response = demandRepository.search(tenantId, demandIds, consumerCodes, requestInfoWrapper,
				businessService);

		if (CollectionUtils.isEmpty(response.getDemands())) {
			return null;
		} else {
			return response.getDemands();
		}
	}

	public List<Demand> updateDemand(RequestInfo requestInfo, List<Demand> demands) {

		return demandRepository.updateDemand(requestInfo, demands);
	}
	
	public List<Demand> cancelDemand(String tenantId, Set<String> demandIds, RequestInfo requestInfo,
			String businessService){
        List<Demand> demands = new LinkedList<>();
        // null as parameter as cancellation  is done on the basis of tenant id 
            List<Demand> searchResult = searchDemand(tenantId,demandIds, null, requestInfo,businessService);
            if(CollectionUtils.isEmpty(searchResult))
                throw new CustomException("INVALID UPDATE","No demand exists for applicationNumber: "); 
            Demand demand = searchResult.get(0);
            	demand.setStatus(StatusEnum.CANCELLED);
            demands.add(demand);
         return demandRepository.updateDemand(requestInfo,demands);
    }

}
