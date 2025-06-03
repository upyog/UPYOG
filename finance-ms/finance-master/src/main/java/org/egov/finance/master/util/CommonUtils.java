package org.egov.finance.master.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.egov.finance.master.exception.MasterServiceException;
import org.egov.finance.master.model.RequestInfo;
import org.egov.finance.master.model.mdms.MasterDetail;
import org.egov.finance.master.model.mdms.MdmsCriteria;
import org.egov.finance.master.model.mdms.MdmsCriteriaReq;
import org.egov.finance.master.model.mdms.ModuleDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import com.jayway.jsonpath.JsonPath;

@Component
public class CommonUtils {

	@Value("${egov.mdms.host}")
	private String mdmshost;

	@Value("${egov.mdms.search.endpoint}")
	private String mdmsSearch;

	private RestTemplate restTemplate;

	@Autowired
	public CommonUtils(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	/*********************** MDMS Utitlity Methods *****************************/

	/**
	 * Fetches all the values of particular attribute as map of fieldname to list
	 *
	 * @param tenantId    tenantId of properties in PropertyRequest
	 * @param names       List of String containing the names of all masterdata
	 *                    whose code has to be extracted
	 * @param requestInfo RequestInfo of the received PropertyRequest
	 * @return Map of MasterData name to the list of code in the MasterData
	 *
	 */
	public Map<String, List<String>> getAttributeValues(String tenantId, String moduleName, List<String> names,
			String filter, String jsonpath) {

		RequestInfo requestInfo = new RequestInfo();
		Object response = null;
		Map<String, String> errormap = new HashMap<>();
		StringBuilder uri = new StringBuilder(mdmshost).append(mdmsSearch);
		MdmsCriteriaReq criteriaReq = prepareMdMsRequest(tenantId, moduleName, names, filter, requestInfo);
		response = restTemplate.postForObject(uri.toString(), criteriaReq, Map.class);
		Optional<Object> mdmsresponse = Optional.ofNullable(response);

		try {
			if (mdmsresponse.isPresent()) {
				return JsonPath.read(mdmsresponse.get(), jsonpath);
			}
		} catch (Exception e) {
			errormap.put("MDMS_ERROR", e.getLocalizedMessage());
		}

		if(!CollectionUtils.isEmpty(errormap))
			throw new MasterServiceException(errormap);
		
		return Collections.emptyMap();
	}

	public MdmsCriteriaReq prepareMdMsRequest(String tenantId, String moduleName, List<String> names, String filter,
			RequestInfo requestInfo) {

		List<MasterDetail> masterDetails = new ArrayList<>();

		names.forEach(name -> {
			masterDetails.add(MasterDetail.builder().name(name).filter(filter).build());
		});

		ModuleDetail moduleDetail = ModuleDetail.builder().moduleName(moduleName).masterDetails(masterDetails).build();
		List<ModuleDetail> moduleDetails = new ArrayList<>();
		moduleDetails.add(moduleDetail);
		MdmsCriteria mdmsCriteria = MdmsCriteria.builder().tenantId(tenantId).moduleDetails(moduleDetails).build();
		return MdmsCriteriaReq.builder().requestInfo(requestInfo).mdmsCriteria(mdmsCriteria).build();
	}

}
