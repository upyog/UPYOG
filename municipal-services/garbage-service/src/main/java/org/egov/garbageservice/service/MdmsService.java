package org.egov.garbageservice.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.garbageservice.model.GarbageAccount;
import org.egov.garbageservice.util.GrbgConstants;
import org.egov.garbageservice.util.RestCallRepository;
import org.egov.mdms.model.MasterDetail;
import org.egov.mdms.model.MdmsCriteria;
import org.egov.mdms.model.MdmsCriteriaReq;
import org.egov.mdms.model.ModuleDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jayway.jsonpath.JsonPath;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestTemplate;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Fetches garbage fee structure, penalty rate, and rebate rate from MDMS for a tenant.
 * Resolves charge amounts from master data during fee calculation and scheduler bill generation.
 */
@Service
@Slf4j
public class MdmsService {

	@Autowired
	RestCallRepository restCallRepository;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private GrbgConstants config;

	@Autowired
	private RestTemplate restTemplate;

	public Object fetchGarbageFeeFromMdms(RequestInfo requestInfo, String tenantId) {

		StringBuilder url = new StringBuilder(config.getMdmsServiceHostUrl()).append(config.getMdmsSearchEndpoint());

		List<ModuleDetail> moduleDetails = new ArrayList<>();
		List<MasterDetail> masterDetails = new ArrayList<>();

		masterDetails.add(MasterDetail.builder().name(GrbgConstants.MDMS_MASTER_NAME_FEE_STRUCTURE).build());
		moduleDetails.add(ModuleDetail.builder().moduleName(GrbgConstants.MDMS_MODULE_NAME_FEE_STRUCTURE)
				.masterDetails(masterDetails).build());

		// Fee structure master is state-level only; strip to state-level tenant for this MDMS lookup
		String stateLevelTenantId = tenantId.contains(".") ? tenantId.split("\\.")[0] : tenantId;

		MdmsCriteria mdmsCriteria = MdmsCriteria.builder().tenantId(stateLevelTenantId).moduleDetails(moduleDetails).build();
		MdmsCriteriaReq mdmsCriteriaReq = MdmsCriteriaReq.builder().requestInfo(requestInfo).mdmsCriteria(mdmsCriteria)
				.build();

		Object mdmsResponse = restCallRepository.fetchResult(url, mdmsCriteriaReq);

		return mdmsResponse;
	}

	public BigDecimal fetchGarbageAmountFromMDMSResponse(Object mdmsResponse, GarbageAccount garbageAccount,
	                                                     List<String> errorList, ObjectNode calculationBreakdown) {

		AtomicReference<BigDecimal> taxAmount = new AtomicReference<>(null);
		List<LinkedHashMap<Object, Object>> feeStructureList = JsonPath.read(
				mdmsResponse, "$.MdmsRes.Garbage.CalculationType");

		feeStructureList.forEach(obj -> {
			Object categoryObj = obj.get("categories");
			Object amountObj = obj.get("amount");

			if (categoryObj == null || amountObj == null) {
				return;
			}

			if (!CollectionUtils.isEmpty(garbageAccount.getGrbgCollectionUnits())
					&& categoryObj.toString().equalsIgnoreCase(
					garbageAccount.getGrbgCollectionUnits().get(0).getCategory())) {

				BigDecimal fee = BigDecimal.valueOf(Double.parseDouble(amountObj.toString()));
				calculationBreakdown.put("fee", fee.toString());
				calculationBreakdown.put("serviceType", String.valueOf(obj.get("serviceType")));
				calculationBreakdown.put("feeType", String.valueOf(obj.get("feeType")));

				taxAmount.set(fee);
			}
		});

		if (taxAmount.get() == null) {
			errorList.add("Category mismatch");
		} else {
			calculationBreakdown.put("final_amount", taxAmount.get().toString());
		}
		return taxAmount.get();
	}

	public BigDecimal fetchGarbagePenaltyRate(RequestInfo requestInfo, String tenantId) {
		// MDMS module and master names
		String moduleName = "ULBS";
		String masterName = "GarbagePenaltyRate";

		// Filter for active entries
		String filter = "$.[?(@.active==true)]";

		// Build MasterDetail list
		MasterDetail masterDetail = MasterDetail.builder()
				.name(masterName)
				.filter(filter)
				.build();
		List<MasterDetail> masterDetails = new ArrayList<>();
		masterDetails.add(masterDetail);

		// Build ModuleDetail list
		ModuleDetail moduleDetail = ModuleDetail.builder()
				.moduleName(moduleName)
				.masterDetails(masterDetails)
				.build();
		List<ModuleDetail> moduleDetails = new ArrayList<>();
		moduleDetails.add(moduleDetail);

		// Build MDMS criteria request
		MdmsCriteria mdmsCriteria = MdmsCriteria.builder()
				.tenantId(tenantId)
				.moduleDetails(moduleDetails)
				.build();
		MdmsCriteriaReq request = MdmsCriteriaReq.builder()
				.requestInfo(requestInfo)
				.mdmsCriteria(mdmsCriteria)
				.build();

		// Call MDMS service
		String url = config.getMdmsV2Host() + config.getMdmsV2SearchEndpoint();
		log.info("[MDMS][Penalty] Fetching GarbagePenaltyRate for tenantId={}", tenantId);
		log.info("[MDMS][Penalty] MdmsCriteriaReq={}", objectMapper.valueToTree(request));

		Object mdmsResponse = restTemplate.postForObject(url, request, Object.class);

		// Parse response
		JsonNode root = objectMapper.convertValue(mdmsResponse, JsonNode.class);
		JsonNode rateNode = root.path("MdmsRes").path(moduleName).path(masterName);

		if (!rateNode.isArray() || rateNode.isEmpty()) {
			log.warn("[MDMS][Penalty] GarbagePenaltyRate not found for tenantId={}", tenantId);
			return BigDecimal.ZERO;
		}

		BigDecimal rate = BigDecimal.valueOf(rateNode.get(0).path("rate").asDouble());
		log.info("[MDMS][Penalty] Penalty rate resolved = {}", rate);

		return rate;
	}
}