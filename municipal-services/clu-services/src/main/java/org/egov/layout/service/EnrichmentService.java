package org.egov.layout.service;

import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.egov.common.contract.request.RequestInfo;
import org.egov.layout.config.CLUConfiguration;
import org.egov.layout.repository.IdGenRepository;
import org.egov.layout.util.CLUConstants;
import org.egov.layout.util.CLUUtil;
import org.egov.layout.web.model.AuditDetails;
import org.egov.layout.web.model.CluRequest;
import org.egov.layout.web.model.Document;
import org.egov.layout.web.model.Clu;
import org.egov.layout.web.model.enums.Status;
import org.egov.layout.web.model.idgen.IdResponse;
import org.egov.layout.web.model.workflow.BusinessService;
import org.egov.layout.web.model.workflow.State;
import org.egov.layout.workflow.WorkflowService;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import net.logstash.logback.encoder.org.apache.commons.lang.StringUtils;

@Service
public class EnrichmentService {

	@Autowired
	private CLUConfiguration config;

	@Autowired
	private CLUUtil nocUtil;

	@Autowired
	private IdGenRepository idGenRepository;

	@Autowired
	private WorkflowService workflowService;







	/**
	 * Enriches the nocReuqest object with puplating the id field with the uuids and
	 * the auditDetails
	 * 
	 * @param nocRequest
	 * @param mdmsData
	 */
	public void enrichCreateRequest(CluRequest nocRequest, Object mdmsData) {
		RequestInfo requestInfo = nocRequest.getRequestInfo();
		AuditDetails auditDetails = nocUtil.getAuditDetails(requestInfo.getUserInfo().getUuid(), true);
		nocRequest.getLayout().setAuditDetails(auditDetails);
		nocRequest.getLayout().getNocDetails().setAuditDetails(auditDetails);
		nocRequest.getLayout().setId(UUID.randomUUID().toString());
		nocRequest.getLayout().getNocDetails().setId(UUID.randomUUID().toString());
		nocRequest.getLayout().getNocDetails().setCluId(nocRequest.getLayout().getId());
		nocRequest.getLayout().setAccountId(UUID.randomUUID().toString());
		nocRequest.getLayout().setCluNo(UUID.randomUUID().toString());

		List<Document> documents = nocRequest.getLayout().getDocuments();



		for (Document doc : documents) {
			doc.setUuid(UUID.randomUUID().toString()); // Set your desired ID here
			doc.setLayoutId(nocRequest.getLayout().getId());
			doc.setDocumentUid(UUID.randomUUID().toString());
			doc.setDocumentAttachment(UUID.randomUUID().toString());
		}

		nocRequest.getLayout().setAccountId(nocRequest.getLayout().getAuditDetails().getCreatedBy());
		setIdgenIds(nocRequest);
		if (!CollectionUtils.isEmpty(nocRequest.getLayout().getDocuments()))
			nocRequest.getLayout().getDocuments().forEach(document -> {
				if (document.getUuid() == null) {
					document.setUuid(UUID.randomUUID().toString());
				}
			});
		if (!ObjectUtils.isEmpty(nocRequest.getLayout().getWorkflow())
				&& !StringUtils.isEmpty(nocRequest.getLayout().getWorkflow().getAction())
				&& nocRequest.getLayout().getWorkflow().getAction().equals(CLUConstants.ACTION_INITIATE)) {

		}
	}

	/**
	 * sets the ids for all the child objects of NOCRequest
	 * @param request
	 */
	private void setIdgenIds(CluRequest request) {
		RequestInfo requestInfo = request.getRequestInfo();
		String tenantId = request.getLayout().getTenantId();
		Clu noc = request.getLayout();

		List<String> applicationNumbers = getIdList(requestInfo, tenantId, config.getApplicationNoIdgenName(), 1);
		ListIterator<String> itr = applicationNumbers.listIterator();

		Map<String, String> errorMap = new HashMap<>();

		if (!errorMap.isEmpty())
			throw new CustomException(errorMap);

		noc.setApplicationNo(itr.next());
	}

	/**
	 * fetch the list of ids based on the params passed
	 * @param requestInfo
	 * @param tenantId
	 * @param idKey
	 * @param count
	 * @return
	 */
	private List<String> getIdList(RequestInfo requestInfo, String tenantId, String idKey, int count) {
		List<IdResponse> idResponses = idGenRepository.getId(requestInfo, tenantId, idKey, count).getIdResponses();

		if (CollectionUtils.isEmpty(idResponses))
			throw new CustomException("IDGEN ERROR", "No ids returned from idgen Service");

		return idResponses.stream().map(IdResponse::getId).collect(Collectors.toList());
	}

	/**
	 * encriches the udpateRequest request Object populating the ids for documents, auditDetails
	 * @param nocRequest
	 * @param searchResult
	 */
	public void enrichNocUpdateRequest(CluRequest nocRequest, Clu searchResult) {

		RequestInfo requestInfo = nocRequest.getRequestInfo();
		AuditDetails auditDetails = nocUtil.getAuditDetails(requestInfo.getUserInfo().getUuid(), false);
		nocRequest.getLayout().setAuditDetails(auditDetails);
		nocRequest.getLayout().getAuditDetails().setLastModifiedTime(auditDetails.getLastModifiedTime());
		List<Document> documents = nocRequest.getLayout().getDocuments();
		// Clu Documents
		if (!CollectionUtils.isEmpty(nocRequest.getLayout().getDocuments())) {
			nocRequest.getLayout().getDocuments().forEach(document -> {
//				if (document.getId() == null) {
//					document.setId(UUID.randomUUID().toString());
//				}



				for (Document doc : documents) {
//					doc.setId(UUID.randomUUID().toString()); // Set your desired ID here
					if (doc.getDocumentUid() == null) {
						doc.setLayoutId(nocRequest.getLayout().getId());
						doc.setDocumentUid(UUID.randomUUID().toString());
//					doc.setDocumentAttachment(UUID.randomUUID().toString());
					}

				}
			});
		}

		if (!ObjectUtils.isEmpty(nocRequest.getLayout().getWorkflow())
				&& !CollectionUtils.isEmpty(nocRequest.getLayout().getWorkflow().getDocuments())) {
			nocRequest.getLayout().getWorkflow().getDocuments().forEach(document -> {



				for (Document doc : documents) {
					if (doc.getDocumentUid() == null){
                    // Set your desired ID here
					doc.setLayoutId(nocRequest.getLayout().getId());
					doc.setDocumentUid(UUID.randomUUID().toString());

					}
				}
			});
		}

		nocRequest.getLayout().setApplicationNo(searchResult.getApplicationNo());

		nocRequest.getLayout().getAuditDetails().setCreatedBy(searchResult.getAuditDetails().getCreatedBy());
		nocRequest.getLayout().getAuditDetails().setCreatedTime(searchResult.getAuditDetails().getCreatedTime());

	}

	/**
	 * called on success of the workflow action. setting the staus based on
	 * applicationStatus updated by workflow and generting the layout number
	 * 
	 * @param nocRequest
	 * @param businessServiceValue
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void postStatusEnrichment(CluRequest nocRequest, String businessServiceValue) {
		Clu noc = nocRequest.getLayout();

		BusinessService businessService = workflowService.getBusinessService(noc, nocRequest.getRequestInfo(),
				businessServiceValue);

		if (businessService != null) {

			State stateObj = workflowService.getCurrentState(noc.getApplicationStatus(), businessService);
			String state = stateObj != null ? stateObj.getState() : StringUtils.EMPTY;

			if (state.equalsIgnoreCase(CLUConstants.APPROVED_STATE)
					|| state.equalsIgnoreCase(CLUConstants.AUTOAPPROVED_STATE)) {

				Map<String, Object> additionalDetail = null;
				if (noc.getNocDetails().getAdditionalDetails() != null) {
					additionalDetail = (Map) noc.getNocDetails().getAdditionalDetails();
				} else {
					additionalDetail = new HashMap<String, Object>();
					noc.getNocDetails().setAdditionalDetails(additionalDetail);
				}

//				List<IdResponse> idResponses = idGenRepository
//						.getId(nocRequest.getRequestInfo(), layout.getTenantId(), config.getApplicationNoIdgenName(), 1)
//						.getIdResponses();
//				layout.setNocNo(idResponses.get(0).getId());
			}
			
			if (state.equalsIgnoreCase("PENDINGSANCTIONPAYMENT") && noc.getWorkflow() != null && noc.getWorkflow().getAction().equalsIgnoreCase(CLUConstants.ACTION_APPROVE)) {
				((Map<String, Object>) noc.getNocDetails().getAdditionalDetails()).put("approvalDate", Long.toString(System.currentTimeMillis()));
			}
			
			if (state.equalsIgnoreCase(CLUConstants.VOIDED_STATUS)) {
				noc.setStatus(Status.INACTIVE);
			}
		}
		
		if (noc.getApplicationStatus().equalsIgnoreCase(CLUConstants.FI_STATUS)) {
			Map<String, String> details = (Map<String, String>) noc.getNocDetails().getAdditionalDetails();
			details.put(CLUConstants.INITIATED_TIME, Long.toString(System.currentTimeMillis()));

			String uniquePropertyId = UUID.randomUUID().toString();
			details.put(CLUConstants.SOURCE_RefId, uniquePropertyId);

			noc.getNocDetails().setAdditionalDetails(details);
		}
	}

}