package org.egov.rl.services.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class RentLeaseUtil extends CommonUtils {
	public com.fasterxml.jackson.databind.node.ArrayNode fetchTaxRatesFromMdms(org.egov.common.contract.request.RequestInfo requestInfo, String tenantId) {
		StringBuilder uri = new StringBuilder();
		uri.append(getConfigs().getMdmsHost()).append(getConfigs().getMdmsEndpoint());

		java.util.List<String> names = java.util.Arrays.asList("TaxRates");
		org.egov.mdms.model.MdmsCriteriaReq mdmsCriteriaReq = prepareMdMsRequest(tenantId, "rentAndLease", names, null, requestInfo);

		try {
			java.util.Optional<Object> response = getRestRepo().fetchResult(uri, mdmsCriteriaReq);
			if (response.isPresent()) {
				com.jayway.jsonpath.DocumentContext documentContext = com.jayway.jsonpath.JsonPath.parse(response.get());
				java.util.List<java.util.Map<String, Object>> taxRates = documentContext.read("$.MdmsRes.rentAndLease.TaxRates");
				com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
				return mapper.valueToTree(taxRates);
			}
		} catch (Exception e) {
			log.error("Exception occurred while fetching TaxRates from MDMS", e);
		}
		return null;
	}
}
