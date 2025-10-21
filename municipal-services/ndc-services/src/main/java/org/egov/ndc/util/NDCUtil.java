package org.egov.ndc.util;

import java.util.*;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.mdms.model.MasterDetail;
import org.egov.mdms.model.MdmsCriteria;
import org.egov.mdms.model.MdmsCriteriaReq;
import org.egov.mdms.model.ModuleDetail;
import org.egov.ndc.config.NDCConfiguration;
import org.egov.ndc.repository.ServiceRequestRepository;
import org.egov.ndc.web.model.AuditDetails;
import org.egov.ndc.web.model.idgen.IdGenerationRequest;
import org.egov.ndc.web.model.idgen.IdGenerationResponse;
import org.egov.ndc.web.model.idgen.IdRequest;
import org.egov.ndc.web.model.idgen.IdResponse;
import org.egov.ndc.web.model.ndc.NdcApplicationRequest;
import org.egov.ndc.web.model.workflow.BusinessService;
import org.egov.ndc.workflow.WorkflowService;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

@Component
public class NDCUtil {

	private NDCConfiguration config;

	@Value("${egov.idgen.host}")
	private String idGenHost;

	@Value("${egov.idgen.path}")
	private String idGenPath;

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private ServiceRequestRepository serviceRequestRepository;

	private WorkflowService workflowService;

	@Autowired
	public NDCUtil(NDCConfiguration config, ServiceRequestRepository serviceRequestRepository, WorkflowService workflowService) {
		this.config = config;
		this.serviceRequestRepository = serviceRequestRepository;
		this.workflowService = workflowService;
	}
	/**
	 * Returns the URL for MDMS search end point
	 *
	 * @return URL for MDMS search end point
	 */
	public StringBuilder getMdmsSearchUrl() {
		return new StringBuilder().append(config.getMdmsHost()).append(config.getMdmsEndPoint());
	}

	/**
	 * prepares the MDMSCriteria to make MDMS Request
	 * @param requestInfo
	 * @param tenantId
	 * @return
	 */
	public MdmsCriteriaReq getMDMSRequest(RequestInfo requestInfo, String tenantId) {
		List<ModuleDetail> moduleRequest = getNDCModuleRequest();

		List<ModuleDetail> moduleDetails = new LinkedList<>();
		moduleDetails.addAll(moduleRequest);

		MdmsCriteria mdmsCriteria = MdmsCriteria.builder().moduleDetails(moduleDetails).tenantId(tenantId).build();

		MdmsCriteriaReq mdmsCriteriaReq = MdmsCriteriaReq.builder().mdmsCriteria(mdmsCriteria).requestInfo(requestInfo)
				.build();
		return mdmsCriteriaReq;
	}
	/**
	 * fetches the ndc documentTypes and ndcTypes mdms data
	 * @return
	 */
	public List<ModuleDetail> getNDCModuleRequest() {
		List<MasterDetail> ndcMasterDtls = new ArrayList<>();

		final String ndcFilterCode = "$.[?(@.isActive==true)]";

		ndcMasterDtls.add(MasterDetail.builder().name(NDCConstants.NDC_TYPE).filter(ndcFilterCode).build());
		ndcMasterDtls.add(MasterDetail.builder().name(NDCConstants.NDC_DOC_TYPE_MAPPING).build());
		ModuleDetail ndcModuleDtls = ModuleDetail.builder().masterDetails(ndcMasterDtls)
				.moduleName(NDCConstants.NDC_MODULE).build();
		
		final String filterCode = "$.[?(@.active==true)]";

		List<MasterDetail> commonMasterDetails = new ArrayList<>();
			commonMasterDetails.add(MasterDetail.builder().name(NDCConstants.DOCUMENT_TYPE).filter(filterCode).build());
		ModuleDetail commonMasterMDtl = ModuleDetail.builder().masterDetails(commonMasterDetails)
				.moduleName(NDCConstants.COMMON_MASTERS_MODULE).build();

		return Arrays.asList(ndcModuleDtls, commonMasterMDtl);
	}

	public List<String> getIdList(RequestInfo requestInfo, String tenantId, String idName, String idformat, Integer count) {
		List<IdRequest> reqList = new ArrayList<>();
		for (int i = 0; i < count; i++) {
			reqList.add(IdRequest.builder().idName(idName).format(idformat).tenantId(tenantId).build());
		}

		IdGenerationRequest request = IdGenerationRequest.builder().idRequests(reqList).requestInfo(requestInfo).build();
		StringBuilder uri = new StringBuilder(idGenHost).append(idGenPath);
		IdGenerationResponse response = mapper.convertValue(serviceRequestRepository.fetchResult(uri, request), IdGenerationResponse.class);

		List<IdResponse> idResponses = response.getIdResponses();

		if (CollectionUtils.isEmpty(idResponses))
			throw new CustomException("IDGEN ERROR", "No ids returned from idgen Service");

		return idResponses.stream().map(IdResponse::getId).collect(Collectors.toList());
	}
}