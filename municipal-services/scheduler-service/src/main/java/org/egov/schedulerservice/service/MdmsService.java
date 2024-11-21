package org.egov.schedulerservice.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.lang3.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.mdms.model.MasterDetail;
import org.egov.mdms.model.MdmsCriteria;
import org.egov.mdms.model.MdmsCriteriaReq;
import org.egov.mdms.model.ModuleDetail;
import org.egov.schedulerservice.contract.garbage.GarbageAccount;
import org.egov.schedulerservice.dto.SchedulerMasterBody;
import org.egov.schedulerservice.util.RestCallRepository;
import org.egov.schedulerservice.util.SchedulerConstants;
import org.javers.common.collections.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jayway.jsonpath.JsonPath;

@Service
public class MdmsService {
	
	@Autowired
	RestCallRepository restCallRepository;
	
	@Autowired
	SchedulerConstants schedulerConstants;

	public Object fetchMdms(SchedulerMasterBody request, RequestInfo requestInfo) {
		
		Object mdmsResponse = null;
		
		if(StringUtils.equalsIgnoreCase(request.getBusinesService(), "GB")) {
			mdmsResponse = fetchGarbageFeeFromMdms(requestInfo);
		}else if(StringUtils.equalsIgnoreCase(request.getBusinesService(), "NewTL")) {
			mdmsResponse = null;
		}
		
		return mdmsResponse;
	}

	public Object fetchGarbageFeeFromMdms(RequestInfo requestInfo) {
		
		StringBuilder url = new StringBuilder(schedulerConstants.getMdmsHost())
									.append(schedulerConstants.getMdmsEndpoint());
		List<ModuleDetail> moduleDetails = new ArrayList<>();
		List<MasterDetail> masterDetails = new ArrayList<>();
		masterDetails.add(MasterDetail.builder().name(SchedulerConstants.MDMS_MASTER_NAME_FEE_STRUCTURE).build());
		moduleDetails.add(ModuleDetail.builder().moduleName(SchedulerConstants.MDMS_MODULE_NAME_FEE_STRUCTURE).masterDetails(masterDetails).build());

		MdmsCriteria mdmsCriteria = MdmsCriteria.builder()
										.tenantId(schedulerConstants.STATE_LEVEL_TENANT_ID)
										.moduleDetails(moduleDetails)
										.build();
		MdmsCriteriaReq mdmsCriteriaReq = MdmsCriteriaReq.builder()
											.requestInfo(requestInfo)
											.mdmsCriteria(mdmsCriteria)
											.build();
		
		Object mdmsResponse = restCallRepository.fetchResult(url, mdmsCriteriaReq);
		
		return mdmsResponse;
	}

	public BigDecimal fetchGarbageAmountFromMDMSResponse(Object mdmsResponse, GarbageAccount garbageAccount) {
		
		AtomicReference<BigDecimal> taxAmount = new AtomicReference<>(BigDecimal.ZERO);
		
		List<LinkedHashMap<Object, Object>> feeStructureList = JsonPath.read(mdmsResponse,
				"$.MdmsRes.Garbage.FeeStructure");
		feeStructureList.stream()
				.filter(fee1 -> StringUtils.equalsIgnoreCase((String) fee1.get("tenantId"), garbageAccount.getTenantId()))
				.forEach(obj -> {
					taxAmount.set(BigDecimal.valueOf(Double.valueOf(obj.get("price").toString())));
				});
		
		return taxAmount.get();
	}

	
	
	
}
