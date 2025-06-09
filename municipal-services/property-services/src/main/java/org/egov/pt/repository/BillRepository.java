package org.egov.pt.repository;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.pt.config.PropertyConfiguration;
import org.egov.pt.models.bill.BillRequest;
import org.egov.pt.models.bill.BillSearchCriteria;
import org.egov.pt.models.bill.GenerateBillCriteria;
import org.egov.pt.models.collection.Bill;
import org.egov.pt.models.collection.BillResponse;
import org.egov.pt.web.contracts.RequestInfoWrapper;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class BillRepository {

	@Autowired
	private RestCallRepository restCallRepository;

	@Autowired
	private PropertyConfiguration config;

	@Autowired
	private ObjectMapper objectMapper;

	public BillResponse fetchBill(GenerateBillCriteria billCriteria, RequestInfo requestInfo) {

		String uri = config.getBillHost().concat(config.getFetchBillEndpoint());
		uri = uri.concat("?consumerCode=").concat(billCriteria.getConsumerCode());
		uri = uri.concat("&tenantId=").concat(billCriteria.getTenantId());
		uri = uri.concat("&businessService=").concat(billCriteria.getBusinessService());

		Object result = restCallRepository.fetchResult(new StringBuilder(uri),
				RequestInfoWrapper.builder().requestInfo(requestInfo).build());
		BillResponse response = null;
		try {
			response = objectMapper.convertValue(result, BillResponse.class);
		} catch (IllegalArgumentException e) {
			throw new CustomException("PARSING ERROR", "Unable to parse response of generate bill");
		}

		return response;
	}

	public List<Bill> searchBill(BillSearchCriteria billCriteria, RequestInfo requestInfo) {
		StringBuilder uriBuilder = new StringBuilder(config.getBillHost()).append(config.getSearchBillEndpoint());

		if (!StringUtils.isEmpty(billCriteria.getTenantId())) {
			uriBuilder.append("?tenantId=").append(billCriteria.getTenantId());
		}

		boolean hasQueryParam = uriBuilder.toString().contains("?");

		if (!StringUtils.isEmpty(billCriteria.getService())) {
			uriBuilder.append(hasQueryParam ? "&" : "?").append("service=").append(billCriteria.getService());
			hasQueryParam = true;
		}

		if (!CollectionUtils.isEmpty(billCriteria.getConsumerCode())) {
			uriBuilder.append(hasQueryParam ? "&" : "?").append("consumerCode=")
					.append(String.join(",", billCriteria.getConsumerCode()));
			hasQueryParam = true;
		}

		if (!CollectionUtils.isEmpty(billCriteria.getBillId())) {
			uriBuilder.append(hasQueryParam ? "&" : "?").append("billId=")
					.append(String.join(",", billCriteria.getBillId()));
			hasQueryParam = true;
		}

		if (billCriteria.getSkipValidation()) {
			uriBuilder.append(hasQueryParam ? "&" : "?").append("skipValidation=").append("true");
			hasQueryParam = true;
		}

		uriBuilder.append(hasQueryParam ? "&" : "?").append("retrieveAll=").append("true");

		Object result = restCallRepository.fetchResult(uriBuilder,
				RequestInfoWrapper.builder().requestInfo(requestInfo).build());

		BillResponse billResponse = objectMapper.convertValue(result, BillResponse.class);
		return billResponse.getBill();
	}
	
	public List<Bill> updateBill(RequestInfo requestInfo, List<Bill> bills) {
		StringBuilder url = new StringBuilder(config.getBillHost());
		url.append(config.getUpdateBillEndpoint());
		BillRequest request = new BillRequest(requestInfo, bills);
		Object result = restCallRepository.fetchResult(url, request);
		BillResponse response = null;
		try {
			response = objectMapper.convertValue(result, BillResponse.class);
		} catch (IllegalArgumentException e) {
			throw new CustomException("PARSING ERROR", "Failed to parse response of update bill");
		}

		return response.getBill();
	}


}
