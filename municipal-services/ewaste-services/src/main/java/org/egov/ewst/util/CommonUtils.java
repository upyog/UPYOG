package org.egov.ewst.util;

import static java.util.Objects.isNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.egov.common.contract.request.RequestInfo;
import org.egov.ewst.config.EwasteConfiguration;
import org.egov.ewst.models.AuditDetails;
import org.egov.ewst.repository.ServiceRequestRepository;
import org.egov.ewst.web.contracts.IdGenerationRequest;
import org.egov.ewst.web.contracts.IdGenerationResponse;
import org.egov.ewst.web.contracts.IdRequest;
import org.egov.ewst.web.contracts.IdResponse;
import org.egov.mdms.model.MasterDetail;
import org.egov.mdms.model.MdmsCriteria;
import org.egov.mdms.model.MdmsCriteriaReq;
import org.egov.mdms.model.ModuleDetail;
import org.egov.tracer.model.CustomException;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jayway.jsonpath.JsonPath;

import lombok.Getter;

@Getter
public class CommonUtils {

	private final ObjectMapper mapper;
	private final EwasteConfiguration configs;
	private final ServiceRequestRepository restRepo;

	protected CommonUtils(ObjectMapper mapper, EwasteConfiguration configs, ServiceRequestRepository restRepo) {
		this.mapper = mapper;
		this.configs = configs;
		this.restRepo = restRepo;
	}

	/**
	 * Method to return auditDetails for create/update flows
	 *
	 * @param by       user identifier performing the action
	 * @param isCreate whether the audit details are for a create flow
	 * @return AuditDetails
	 */
	public AuditDetails getAuditDetails(String by, Boolean isCreate) {

		Long time = System.currentTimeMillis();

		if (Boolean.TRUE.equals(isCreate))
			return AuditDetails.builder().createdBy(by).lastModifiedBy(by).createdTime(time).lastModifiedTime(time)
					.build();
		else
			return AuditDetails.builder().lastModifiedBy(by).lastModifiedTime(time).build();
	}

	/* **************************** ID GEN **************************** */

	/**
	 * Returns a list of numbers generated from idgen
	 * 
	 * @param requestInfo RequestInfo from the request
	 * @param tenantId    tenantId of the city
	 * @param idName      code of the field defined in application properties for
	 *                    which ids are generated for
	 * @param idformat    format in which ids are to be generated
	 * @param count       Number of ids to be generated
	 * @return List of ids generated using idGen service
	 */
	public List<String> getIdList(RequestInfo requestInfo, String tenantId, String idName, String idformat, int count) {

		List<IdRequest> reqList = new ArrayList<>();
		for (int i = 0; i < count; i++) {
			reqList.add(IdRequest.builder().idName(idName).format(idformat).tenantId(tenantId).build());
		}

		IdGenerationRequest request = IdGenerationRequest.builder().idRequests(reqList).requestInfo(requestInfo)
				.build();
		StringBuilder uri = new StringBuilder(configs.getIdGenHost()).append(configs.getIdGenPath());
		IdGenerationResponse response = mapper.convertValue(restRepo.fetchResult(uri, request).get(),
				IdGenerationResponse.class);

		List<IdResponse> idResponses = response.getIdResponses();

		if (CollectionUtils.isEmpty(idResponses))
			throw new CustomException("IDGEN ERROR", "No ids returned from idgen Service");

		return idResponses.stream().map(IdResponse::getId).toList();
	}

	/* *********************** MDMS Utitlity Methods ***************************** */

	/**
	 * Fetches all the values of particular attribute as map of fieldname to list
	 *
	 * @param tenantId    tenantId from ewaste request
	 * @param moduleName  MDMS module name to query
	 * @param names       List of String containing the names of all masterdata
	 *                    whose code has to be extracted
	 * @param filter      filter expression for master data
	 * @param jsonpath    json path to extract values from MDMS response
	 * @param requestInfo RequestInfo of the received Ewaste request
	 * @return Map of MasterData name to the list of code in the MasterData
	 *
	 */
	public Map<String, List<String>> getAttributeValues(String tenantId, String moduleName, List<String> names,
			String filter, String jsonpath, RequestInfo requestInfo) {

		StringBuilder uri = new StringBuilder(configs.getMdmsHost()).append(configs.getMdmsEndpoint());
		MdmsCriteriaReq criteriaReq = prepareMdMsRequest(tenantId, moduleName, names, filter, requestInfo);
		Optional<Object> response = restRepo.fetchResult(uri, criteriaReq);

		try {
			if (response.isPresent()) {
				return JsonPath.read(response.get(), jsonpath);
			}
		} catch (Exception e) {
			throw new CustomException(ErrorConstants.INVALID_TENANT_ID_MDMS_KEY,
					ErrorConstants.INVALID_TENANT_ID_MDMS_MSG);
		}

		return Collections.emptyMap();
	}

	public MdmsCriteriaReq prepareMdMsRequest(String tenantId, String moduleName, List<String> names, String filter,
			RequestInfo requestInfo) {

		List<MasterDetail> masterDetails = new ArrayList<>();

		names.forEach(name -> masterDetails.add(MasterDetail.builder().name(name).filter(filter).build()));

		ModuleDetail moduleDetail = ModuleDetail.builder().moduleName(moduleName).masterDetails(masterDetails).build();
		List<ModuleDetail> moduleDetails = new ArrayList<>();
		moduleDetails.add(moduleDetail);
		MdmsCriteria mdmsCriteria = MdmsCriteria.builder().tenantId(tenantId).moduleDetails(moduleDetails).build();
		return MdmsCriteriaReq.builder().requestInfo(requestInfo).mdmsCriteria(mdmsCriteria).build();
	}

	/* JSON MERGE UTILITY METHODS */

	/**
	 * Method to merge additional details during update
	 * 
	 * @param mainNode   base JSON node
	 * @param updateNode JSON node containing updates
	 * @return merged JSON node
	 */
	public JsonNode jsonMerge(JsonNode mainNode, JsonNode updateNode) {

		if (isNull(mainNode) || mainNode.isNull())
			return updateNode;
		if (isNull(updateNode) || updateNode.isNull())
			return mainNode;

		Iterator<String> fieldNames = updateNode.fieldNames();
		while (fieldNames.hasNext()) {

			String fieldName = fieldNames.next();
			JsonNode jsonNode = mainNode.get(fieldName);
			if (jsonNode != null && jsonNode.isObject()) {
				jsonMerge(jsonNode, updateNode.get(fieldName));
			} else if (mainNode instanceof ObjectNode objectNode) {
				JsonNode value = updateNode.get(fieldName);
				objectNode.set(fieldName, value);
			}

		}
		return mainNode;
	}
}
