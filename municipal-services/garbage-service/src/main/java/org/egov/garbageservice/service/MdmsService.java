package org.egov.garbageservice.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

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

@Service
public class MdmsService {

	@Autowired
	RestCallRepository restCallRepository;

	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private GrbgConstants config;

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

	public BigDecimal fetchGarbageAmountFromMDMSResponse(Object mdmsResponse, GarbageAccount garbageAccount,ObjectNode errorMap,ObjectNode  calculationBreakdown) {

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
									errorMap.put("ZERO_UNIT_CHARGE","Per Unit Charge Should Not Be Zero");
								}
							}else{
								BigDecimal flatCharge =  BigDecimal.valueOf(Double.valueOf(obj.get("fixedUnitCost").toString()));
								if(flatCharge.compareTo(BigDecimal.valueOf(0)) > 0) {
									calculationBreakdown.put("fixedUnitCost",flatCharge.toString());
									fee = fee.add(flatCharge);
								}else{
									errorMap.put("ZERO_FLAT_CHARGE","Flat Charges Should Not Be Zero");
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
								errorMap.put("ZERO_BPL_REBATE","BPL Rebate Cannot Be Zero");
							}
						}
						taxAmount.set(fee);
					}
				});
		calculationBreakdown.put("final_amount",taxAmount.get().toString());
		if(taxAmount.get() == null) {
			errorMap.put("category_mismatch","category,subcategory or subcategorytype mismatch");
		}
		return taxAmount.get();
	}

}