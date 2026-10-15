package org.egov.garbageservice.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.lang3.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.garbageservice.model.DdpPrintingUlbWard;
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

		MdmsCriteria mdmsCriteria = MdmsCriteria.builder().tenantId(tenantId).moduleDetails(moduleDetails).build();
		MdmsCriteriaReq mdmsCriteriaReq = MdmsCriteriaReq.builder().requestInfo(requestInfo).mdmsCriteria(mdmsCriteria)
				.build();

		Object mdmsResponse = restCallRepository.fetchResult(url, mdmsCriteriaReq);

		return mdmsResponse;
	}

	public BigDecimal fetchGarbageAmountFromMDMSResponse(Object mdmsResponse, GarbageAccount garbageAccount,List<String> errorList,ObjectNode  calculationBreakdown) {

		AtomicReference<BigDecimal> taxAmount = new AtomicReference<>(null);
		List<LinkedHashMap<Object, Object>> feeStructureList = JsonPath.read(mdmsResponse,
				"$.MdmsRes.Garbage.FeeStructure");
		feeStructureList.stream().filter(
				fee1 -> StringUtils.equalsIgnoreCase((String) fee1.get("tenantId"), garbageAccount.getTenantId()))
				.forEach(obj -> {
					if (!CollectionUtils.isEmpty(garbageAccount.getGrbgCollectionUnits())
							&& obj.get("categories").toString()
									.equalsIgnoreCase(garbageAccount.getGrbgCollectionUnits().get(0).getCategory())
							&& obj.get("subcategories").toString()
									.equalsIgnoreCase(garbageAccount.getGrbgCollectionUnits().get(0).getSubCategory())
							&& (!StringUtils
									.isEmpty(garbageAccount.getGrbgCollectionUnits().get(0).getSubCategoryType())
									&& null != obj.get("subcategorytype")
									&& obj.get("subcategorytype").toString().equalsIgnoreCase(
											garbageAccount.getGrbgCollectionUnits().get(0).getSubCategoryType()))) {
						BigDecimal fee = BigDecimal.valueOf(Double.valueOf(obj.get("fee").toString()));
						
						calculationBreakdown.put("fee", fee.toString());
						calculationBreakdown.put("subcategorytype", garbageAccount.getGrbgCollectionUnits().get(0).getSubCategoryType());
//						taxAmount.set(BigDecimal.valueOf(Double.valueOf(obj.get("fee").toString())));
						//this condition needs to be changed for or on the basis of categorization
						
						if(Boolean.valueOf(obj.get("isAdditonalCostAdded").toString())) {
							if(garbageAccount.getGrbgCollectionUnits().get(0).getIsvariablecalculation()) {
								BigDecimal perUnitCharge =  BigDecimal.valueOf(Double.valueOf(obj.get("variableUnitCost").toString()));
								if(perUnitCharge.compareTo(BigDecimal.valueOf(0)) > 0) {
									fee = fee.add(perUnitCharge.multiply(BigDecimal.valueOf(garbageAccount.getGrbgCollectionUnits().get(0).getNo_of_units())));
									calculationBreakdown.put("variableUnitCost", perUnitCharge.toString());
									calculationBreakdown.put("no_of_units",BigDecimal.valueOf(garbageAccount.getGrbgCollectionUnits().get(0).getNo_of_units()).toString());
								}else{
									errorList.add("Per Unit Charge Should Not Be Zero");
								}
							}else{
								BigDecimal flatCharge =  BigDecimal.valueOf(Double.valueOf(obj.get("fixedUnitCost").toString()));
								if(flatCharge.compareTo(BigDecimal.valueOf(0)) > 0) {
									calculationBreakdown.put("fixedUnitCost",flatCharge.toString());
									fee = fee.add(flatCharge);
								}else{
									errorList.add("Flat Charges Should Not Be Zero");
								}
							}
						}
						if(garbageAccount.getGrbgCollectionUnits().get(0).getIsbplunit()) {
							BigDecimal bplRebate =   BigDecimal.valueOf(Double.valueOf(obj.get("bplRebatePercentage").toString()));
							if(bplRebate.compareTo(BigDecimal.valueOf(0)) > 0) {
								calculationBreakdown.put("bplRebatePrcnt",bplRebate.toString());
								calculationBreakdown.put("bplRebate",fee.multiply(bplRebate).divide(BigDecimal.valueOf(100)).toString());
								fee = fee.subtract(fee.multiply(bplRebate).divide(BigDecimal.valueOf(100)));
							}else {
								errorList.add("BPL Rebate Cannot Be Zero");
							}
						}
						taxAmount.set(fee);
					}
				});
		if(taxAmount.get() == null) {
			errorList.add("Category mismatch");
		}else {
			calculationBreakdown.put("final_amount",taxAmount.get().toString());
		}
		return taxAmount.get();
	}
	
	private Object fetchMdmsData(
	        RequestInfo requestInfo,
	        String tenantId,
	        String moduleName,
	        String masterName) {
	
	    String url = config.getMdmsServiceHostUrl() + config.getMdmsSearchEndpoint();
	
	    MasterDetail masterDetail =
	            MasterDetail.builder().name(masterName).build();
	    
	    List<MasterDetail> masterDetails = new ArrayList<>();
	    masterDetails.add(masterDetail);

	    ModuleDetail moduleDetail =
	            ModuleDetail.builder()
	                    .moduleName(moduleName)
	                    .masterDetails(masterDetails)
	                    .build();

	    List<ModuleDetail> moduleDetails = new ArrayList<>();
	    moduleDetails.add(moduleDetail);

	    MdmsCriteria mdmsCriteria =
	            MdmsCriteria.builder()
	                    .tenantId(tenantId)
	                    .moduleDetails(moduleDetails)
	                    .build();
	
	    MdmsCriteriaReq request =
	            MdmsCriteriaReq.builder()
	                    .requestInfo(requestInfo)
	                    .mdmsCriteria(mdmsCriteria)
	                    .build();
	
	    log.info("[MDMS][Penalty] MDMS URL={}", url);

	    log.info("[MDMS][Penalty] tenantId={}", tenantId);
	    log.info("[MDMS][Penalty] moduleName={}", moduleName);
	    log.info("[MDMS][Penalty] masterName={}", masterName);

	    log.info("[MDMS][Penalty] MasterDetails={}",
	            objectMapper.valueToTree(masterDetails).toString());

	    log.info("[MDMS][Penalty] ModuleDetails={}",
	            objectMapper.valueToTree(moduleDetails).toString());

	    log.info("[MDMS][Penalty] MdmsCriteria={}",
	            objectMapper.valueToTree(mdmsCriteria).toString());

	    log.info("[MDMS][Penalty] MdmsCriteriaReq={}",
	            objectMapper.valueToTree(request).toString());

	    
	    
	    return restTemplate.postForObject(url, request, Object.class);
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
	
	public BigDecimal fetchGarbageRebateRate(RequestInfo requestInfo, String tenantId) {

	    String moduleName = "ULBS";
	    String masterName = "GarbageRebate";

	    String filter = "$.[?(@.active==true)]";

	    MasterDetail masterDetail = MasterDetail.builder()
	            .name(masterName)
	            .filter(filter)
	            .build();

	    ModuleDetail moduleDetail = ModuleDetail.builder()
	            .moduleName(moduleName)
	            .masterDetails(Collections.singletonList(masterDetail))
	            .build();

	    MdmsCriteria mdmsCriteria = MdmsCriteria.builder()
	            .tenantId(tenantId)
	            .moduleDetails(Collections.singletonList(moduleDetail))
	            .build();

	    MdmsCriteriaReq request = MdmsCriteriaReq.builder()
	            .requestInfo(requestInfo)
	            .mdmsCriteria(mdmsCriteria)
	            .build();

	    String url = config.getMdmsV2Host() + config.getMdmsV2SearchEndpoint();

	    Object mdmsResponse = restTemplate.postForObject(url, request, Object.class);

	    JsonNode root = objectMapper.convertValue(mdmsResponse, JsonNode.class);
	    JsonNode rebateArray = root.path("MdmsRes").path(moduleName).path(masterName);

	    if (!rebateArray.isArray() || rebateArray.isEmpty()) {
	        log.warn("[MDMS][Rebate] GarbageRebate not found for tenantId={}", tenantId);
	        return BigDecimal.ZERO;
	    }

	    BigDecimal rate = rebateArray.get(0).path("rate").decimalValue();
	    log.info("[MDMS][Rebate] Rebate rate resolved = {}", rate);

	    return rate;
	}

	/**
	 * Fetches every active ULB/ward combination enabled for door plate
	 * printing from the ULBS.DdpPrinting MDMS v2 master that was added
	 * <b>today</b> (per the record's {@code auditDetails.createdTime}, in
	 * the system default zone), paging through the full result set. The
	 * request body is built as a plain map (rather than the
	 * org.egov.mdms.model v1 client types used elsewhere in this class)
	 * because the mdms-v2 search endpoint expects the schemaCode/filterMap-
	 * based criteria shape, not the v1 moduleName/masterDetails shape.
	 *
	 * <p>The master's {@code data} JSON is expected to look like
	 * {@code {"count": 1, "ulbName": "Arki", "wardName": "Jhinwar Muhalla"}};
	 * {@code count} is not used by this method.
	 */
	public List<DdpPrintingUlbWard> fetchDdpPrintingUlbWards(RequestInfo requestInfo, String tenantId) {

		List<DdpPrintingUlbWard> ulbWards = new ArrayList<>();
		String url = config.getMdmsV2Host() + config.getMdmsV2SearchEndpoint();
		int limit = 100;
		int offset = 0;
		LocalDate today = LocalDate.now(ZoneId.systemDefault());

		while (true) {
			Map<String, Object> filterMap = new LinkedHashMap<>();
			filterMap.put("active", true);

			Map<String, Object> mdmsCriteria = new LinkedHashMap<>();
			mdmsCriteria.put("tenantId", tenantId);
			mdmsCriteria.put("schemaCode", GrbgConstants.MDMS_SCHEMA_CODE_DDP_PRINTING);
			mdmsCriteria.put("filterMap", filterMap);
			mdmsCriteria.put("isActive", true);
			mdmsCriteria.put("offset", offset);
			mdmsCriteria.put("limit", limit);

			Map<String, Object> request = new LinkedHashMap<>();
			request.put("RequestInfo", requestInfo);
			request.put("MdmsCriteria", mdmsCriteria);

			log.info("[MDMS][DdpPrinting] Fetching ULB/ward list, offset={}, limit={}", offset, limit);

			Object response = restTemplate.postForObject(url, request, Object.class);
			JsonNode root = objectMapper.convertValue(response, JsonNode.class);
			JsonNode mdmsArray = root.path("mdms");

			if (!mdmsArray.isArray() || mdmsArray.isEmpty()) {
				break;
			}

			for (JsonNode record : mdmsArray) {
				long createdTime = record.path("auditDetails").path("createdTime").asLong(-1);
				if (createdTime < 0) {
					continue;
				}
				LocalDate createdDate = Instant.ofEpochMilli(createdTime).atZone(ZoneId.systemDefault())
						.toLocalDate();
				if (!today.equals(createdDate)) {
					continue;
				}

				JsonNode data = record.path("data");
				String ulbName = data.path("ulbName").asText(null);
				String wardName = data.path("wardName").asText(null);
				if (StringUtils.isNotEmpty(ulbName) && StringUtils.isNotEmpty(wardName)) {
					ulbWards.add(DdpPrintingUlbWard.builder().ulbName(ulbName).wardName(wardName).build());
				}
			}

			if (mdmsArray.size() < limit) {
				break;
			}
			offset += limit;
		}

		log.info("[MDMS][DdpPrinting] Resolved {} ULB/ward combination(s) added today for tenantId={}",
				ulbWards.size(), tenantId);

		return ulbWards;
	}

}