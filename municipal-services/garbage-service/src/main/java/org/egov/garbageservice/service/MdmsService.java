package org.egov.garbageservice.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
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

import com.jayway.jsonpath.JsonPath;

@Service
public class MdmsService {

	@Autowired
	RestCallRepository restCallRepository;

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

	public BigDecimal fetchGarbageAmountFromMDMSResponse(Object mdmsResponse, GarbageAccount garbageAccount) {

		AtomicReference<BigDecimal> taxAmount = new AtomicReference<>(BigDecimal.ZERO);

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
						taxAmount.set(BigDecimal.valueOf(Double.valueOf(obj.get("fee").toString())));
					}
				});

		return taxAmount.get();
	}

}