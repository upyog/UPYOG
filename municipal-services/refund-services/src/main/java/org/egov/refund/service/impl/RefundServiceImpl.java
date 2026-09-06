package org.egov.refund.service.impl;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.Role;
import org.egov.common.contract.request.User;
import org.egov.common.contract.response.ResponseInfo;
import org.egov.refund.Repository.RefundRepository;
import org.egov.refund.config.ApplicationProperties;
import org.egov.refund.model.AuditDetails;
import org.egov.refund.model.Refund;
import org.egov.refund.model.WorkflowTransition;
import org.egov.refund.querybuilder.RefundSearchCriteria;
import org.egov.refund.service.FinanceService;
import org.egov.refund.service.PaymentRefundService;
import org.egov.refund.service.RefundAuditService;
import org.egov.refund.service.RefundNumberService;
import org.egov.refund.service.RefundService;
import org.egov.refund.service.WorkflowService;
import org.egov.refund.util.RefundConstants;
import org.egov.refund.web.contracat.PaymentWorkflow;
import org.egov.refund.web.contracat.PaymentWorkflowRequest;
import org.egov.refund.web.contracat.RefundActionRequest;
import org.egov.refund.web.contracat.RefundGetRequest;
import org.egov.refund.web.contracat.RefundRequest;
import org.egov.refund.web.contracat.RefundResponse;
import org.egov.refund.web.contracat.RefundSearchRequest;
import org.egov.refund.web.contracat.RefundSearchResponse;
import org.egov.refund.web.factory.ResponseInfoFactory;
import org.egov.tracer.model.CustomException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class RefundServiceImpl implements RefundService {

	private final ApplicationProperties applicationProperties;
	private final RefundRepository refundRepository;
	private final WorkflowService workflowService;
	private final FinanceService financeService;
	private final RefundNumberService numberService;
	private final PaymentRefundService paymentRefundService;
	private final ObjectMapper objectMapper;
	private final RefundAuditService refundAuditService;

	public RefundServiceImpl(RefundRepository refundRepository, WorkflowService workflowService,
			FinanceService financeService, RefundNumberService numberService, PaymentRefundService paymentRefundService,
			ObjectMapper objectMapper, ApplicationProperties applicationProperties,
			RefundAuditService refundAuditService) {

		this.refundRepository = refundRepository;
		this.workflowService = workflowService;
		this.financeService = financeService;
		this.numberService = numberService;
		this.paymentRefundService = paymentRefundService;
		this.objectMapper = objectMapper;
		this.applicationProperties = applicationProperties;
		this.refundAuditService = refundAuditService;
	}

	// ============================================================
	// CREATE
	// ============================================================

	@Override
	@Transactional
	public RefundResponse create(RefundRequest request) {

		validateRequestInfo(request);
		validateCreateRequest(request.getRefund());

		Refund inputRefund = request.getRefund();

		UUID refundId = UUID.randomUUID();
		long currentTime = System.currentTimeMillis();

		String userId = getUserId(request.getRequestInfo());

		String refundNo = numberService.generateRefundNo(inputRefund.getModuleName(), inputRefund.getBusinessService(),
				inputRefund.getConsumerCode());

		Refund refund = Refund.builder().id(refundId).refundNo(refundNo).tenantId(inputRefund.getTenantId())
				.moduleName(inputRefund.getModuleName()).businessService(inputRefund.getBusinessService())
				.consumerCode(inputRefund.getConsumerCode()).paymentId(inputRefund.getPaymentId())
				.applicantName(inputRefund.getApplicantName()).mobileNumber(inputRefund.getMobileNumber())
				.refundCategory(inputRefund.getRefundCategory()).refundReason(inputRefund.getRefundReason())
				.paymentModeOriginal(inputRefund.getPaymentModeOriginal())
				.amountPaid(defaultAmount(inputRefund.getAmountPaid()))
				.refundAmount(defaultAmount(inputRefund.getRefundAmount())).refundMode(resolveRefundMode(inputRefund))
				.fileStoreId(inputRefund.getFileStoreId()).status(RefundConstants.ACTION_SUBMITTED)
				.sanctionRef(inputRefund.getSanctionRef()).financeApprovalDate(inputRefund.getFinanceApprovalDate())
				.gatewayRefundId(inputRefund.getGatewayRefundId())
				.beneficiaryDetails(inputRefund.getBeneficiaryDetails())
				.additionalDetails(inputRefund.getAdditionalDetails())
				.auditDetails(buildAuditDetails(userId, currentTime)).build();

		refundRepository.save(refund);

		RefundActionRequest actionRequest = RefundActionRequest.builder().action(RefundConstants.STATUS_INITIATE)
				.userId(userId).requestInfo(request.getRequestInfo()).build();

		RefundResponse response = processInternal(refundId, actionRequest);

		response.setResponseInfo(ResponseInfoFactory.createResponseInfoFromRequestInfo(request.getRequestInfo(), true));

		return response;
	}

	// ============================================================
	// UPDATE
	// ============================================================

	@Override
	@Transactional
	public RefundResponse update(RefundRequest request) {

		validateRequestInfo(request);
		validateUpdateRequest(request.getRefund());

		Refund inputRefund = request.getRefund();

		Refund refund = refundRepository.findById(inputRefund.getId());

		if (refund == null) {
			throw new IllegalArgumentException("Refund not found for id: " + inputRefund.getId());
		}

		String userId = getUserId(request.getRequestInfo());

		/*
		 * Update refund fields received from module.
		 */
		updateRefundFields(refund, inputRefund);

		/*
		 * Persist latest refund data including processInstance.
		 */
		refundRepository.update(refund);

		/*
		 * Process workflow only when processInstance is provided.
		 */
		if (inputRefund.getProcessInstance() != null && !isBlank(inputRefund.getProcessInstance().getAction())) {

			String action = inputRefund.getProcessInstance().getAction();

			/*
			 * Finance approval/rejection handling.
			 */
			if (RefundConstants.STATUS_PENDING_WITH_FINANCE.equalsIgnoreCase(refund.getStatus())) {

				RequestInfo systemRequestInfo = createSystemRequestInfo();

				String nextAction;

				if (RefundConstants.ACTION_APPROVE.equalsIgnoreCase(action)) {
					nextAction = RefundConstants.ACTION_REFUND_INITIATE;
				} else if (RefundConstants.ACTION_REJECT.equalsIgnoreCase(action)) {
					nextAction = RefundConstants.ACTION_REJECT;
				} else {
					return null; // or continue with existing action validation/handling
				}

				RefundActionRequest nextActionRequest = RefundActionRequest.builder().action(nextAction)
						.userId(systemRequestInfo.getUserInfo().getUuid()).requestInfo(systemRequestInfo).build();

				RefundResponse response = processInternal(refund.getId(), nextActionRequest);

				response.setResponseInfo(
						ResponseInfoFactory.createResponseInfoFromRequestInfo(request.getRequestInfo(), true));

				return response;
			}

			if (RefundConstants.ACTION_APPROVE.equalsIgnoreCase(action)) {

				RefundActionRequest approveRequest = RefundActionRequest.builder()
						.action(RefundConstants.ACTION_APPROVE).userId(userId).requestInfo(request.getRequestInfo())
						.build();

				RefundResponse response = processInternal(refund.getId(), approveRequest);

				/*
				 * Get latest refund after APPROVE transition.
				 */
				Refund approvedRefund = response.getRefund();

				/*
				 * Trigger next action after approval.
				 */
				response = processApproval(approvedRefund, request.getRequestInfo(), userId);

				response.setResponseInfo(
						ResponseInfoFactory.createResponseInfoFromRequestInfo(request.getRequestInfo(), true));

				return response;
			}

			/*
			 * Normal workflow processing.
			 *
			 * processInstance/action from the module is sent to workflow validation.
			 */
			RefundActionRequest actionRequest = RefundActionRequest.builder().action(action).userId(userId)
					.requestInfo(request.getRequestInfo()).build();

			RefundResponse response = processInternal(refund.getId(), actionRequest);

			response.setResponseInfo(
					ResponseInfoFactory.createResponseInfoFromRequestInfo(request.getRequestInfo(), true));

			return response;
		}

		/*
		 * No workflow action. Only refund data was updated.
		 */
		RefundResponse response = toResponse(refund);

		response.setResponseInfo(ResponseInfoFactory.createResponseInfoFromRequestInfo(request.getRequestInfo(), true));

		return response;
	}

	// ============================================================
	// GET
	// ============================================================

	@Override
	public RefundResponse get(RefundGetRequest request) {

		validateGetRequest(request);

		Refund refund;

		if (!isBlank(request.getId())) {
			refund = refundRepository.findById(UUID.fromString(request.getId()));
		} else {
			refund = refundRepository.findByRefundNo(request.getRefundNo());
		}

		if (refund == null) {
			throw new IllegalArgumentException("Refund not found");
		}

		RefundResponse response = toResponse(refund);

		response.setResponseInfo(ResponseInfoFactory.createResponseInfoFromRequestInfo(request.getRequestInfo(), true));

		return response;
	}

	// ============================================================
	// SEARCH
	// ============================================================

	@Override
	public RefundSearchResponse search(RefundSearchRequest request) {

		validateSearchRequest(request);

		RefundSearchCriteria criteria = RefundSearchCriteria.builder().tenantId(request.getTenantId())
				.moduleName(request.getModuleName()).businessService(request.getBusinessService())
				.consumerCode(request.getConsumerCode()).paymentId(request.getPaymentId())
				.refundNo(request.getRefundNo()).status(request.getStatus()).refundCategory(request.getRefundCategory())
				.gatewayRefundId(request.getGatewayRefundId()).sanctionRef(request.getSanctionRef()).build();

		List<Refund> refunds = refundRepository.search(criteria);

		List<RefundResponse> responses = refunds.stream().map(this::toResponse).toList();

		ResponseInfo responseInfo = ResponseInfoFactory.createResponseInfoFromRequestInfo(request.getRequestInfo(),
				true);

		return RefundSearchResponse.builder().responseInfo(responseInfo).refunds(responses).totalCount(responses.size())
				.build();
	}

	// ============================================================
	// WORKFLOW PROCESS
	// ============================================================

	@Override
	@Transactional
	public RefundResponse process(RefundActionRequest request) {

		RefundResponse response = processInternal(request.getId(), request);

		response.setResponseInfo(ResponseInfoFactory.createResponseInfoFromRequestInfo(request.getRequestInfo(), true));

		return response;
	}

	private RefundResponse processInternal(UUID id, RefundActionRequest request) {

		Refund refund = refundRepository.findById(id);

		if (refund == null) {
			throw new IllegalArgumentException("Refund not found for id: " + id);
		}

		validateActionRequest(request);

		WorkflowTransition transition = workflowService.validateAndGetNextState(refund, request);

		if (transition == null || !transition.isValid()) {
			throw new IllegalArgumentException(
					"Action " + request.getAction() + " is not allowed from status " + refund.getStatus());
		}

		// Update workflow status
		refundRepository.updateStatus(id, transition.getApplicationStatus(), request.getUserId());

		// Get latest refund state
		Refund updatedRefund = refundRepository.findById(id);

		if (updatedRefund == null) {
			throw new IllegalStateException("Refund not found after status update: " + id);
		}

		// Create audit for workflow transition
		refundAuditService.createAudit(updatedRefund, transition.getAction());

		// Finance integration
		if (RefundConstants.ACTION_CREATE_REQUEST.equals(transition.getAction())) {

			RefundRequest refundRequest = RefundRequest.builder().refund(updatedRefund)
					.requestInfo(createSystemRequestInfo()).build();

			financeService.processRefund(refundRequest);
		}

		// Payment refund
		if (RefundConstants.ACTION_REFUND_INITIATE.equals(transition.getAction())) {

			updatedRefund = processRefundBasedOnMode(updatedRefund, request);
		}

		return toResponse(updatedRefund);
	}

	// ============================================================
	// APPROVAL
	// ============================================================

	private RefundResponse processApproval(Refund refund, RequestInfo requestInfo, String userId) {

		if (refund == null) {
			throw new IllegalArgumentException("Refund cannot be null for approval processing");
		}

		String nextAction = applicationProperties.isSendToFinance() ? RefundConstants.ACTION_CREATE_REQUEST
				: RefundConstants.ACTION_REFUND_INITIATE;

		/*
		 * Create system/internal workflow action.
		 */
		RefundActionRequest nextActionRequest = RefundActionRequest.builder().action(nextAction).userId(userId)
				.requestInfo(requestInfo).build();

		/*
		 * Process next workflow action.
		 */
		return processInternal(refund.getId(), nextActionRequest);
	}

	// ============================================================
	// REFUND MODE
	// ============================================================

	private Refund processRefundBasedOnMode(Refund refund, RefundActionRequest request) {

		String refundMode = refund.getRefundMode();

		if (RefundConstants.PAYMENT_MODE_ONLINE.equalsIgnoreCase(refundMode)) {

			updateAuditDetails(refund, request.getUserId(), System.currentTimeMillis());

			refundRepository.update(refund);

			paymentRefundService.initiateRefund(buildPaymentWorkflowRequest(refund, request));

			return refund;
		}

		if (RefundConstants.REFUND_MODE_OFFLINE.equalsIgnoreCase(refundMode)) {

			updateAuditDetails(refund, request.getUserId(), System.currentTimeMillis());

			refundRepository.update(refund);

			return refund;
		}

		throw new IllegalArgumentException("Invalid refund mode: " + refundMode);
	}

	// ============================================================
	// REFUND FIELD UPDATE
	// ============================================================

	private void updateRefundFields(Refund refund, Refund inputRefund) {
		if (refund == null || inputRefund == null) {
			return;
		}

		// Identifiers & Meta parameters
		if (inputRefund.getId() != null)
			refund.setId(inputRefund.getId());
		if (inputRefund.getRefundNo() != null)
			refund.setRefundNo(inputRefund.getRefundNo());
		if (inputRefund.getTenantId() != null)
			refund.setTenantId(inputRefund.getTenantId());
		if (inputRefund.getModuleName() != null)
			refund.setModuleName(inputRefund.getModuleName());
		if (inputRefund.getBusinessService() != null)
			refund.setBusinessService(inputRefund.getBusinessService());
		if (inputRefund.getConsumerCode() != null)
			refund.setConsumerCode(inputRefund.getConsumerCode());
		if (inputRefund.getPaymentId() != null)
			refund.setPaymentId(inputRefund.getPaymentId());

		// Applicant & Refund Details
		if (inputRefund.getApplicantName() != null)
			refund.setApplicantName(inputRefund.getApplicantName());
		if (inputRefund.getMobileNumber() != null)
			refund.setMobileNumber(inputRefund.getMobileNumber());
		if (inputRefund.getRefundCategory() != null)
			refund.setRefundCategory(inputRefund.getRefundCategory());
		if (inputRefund.getRefundReason() != null)
			refund.setRefundReason(inputRefund.getRefundReason());
		if (inputRefund.getPaymentModeOriginal() != null)
			refund.setPaymentModeOriginal(inputRefund.getPaymentModeOriginal());

		if (inputRefund.getAmountPaid() != null)
			refund.setAmountPaid(defaultAmount(inputRefund.getAmountPaid()));
		if (inputRefund.getRefundAmount() != null)
			refund.setRefundAmount(defaultAmount(inputRefund.getRefundAmount()));

		String resolvedMode = resolveRefundMode(inputRefund);
		if (resolvedMode != null)
			refund.setRefundMode(resolvedMode);

		if (inputRefund.getStatus() != null)
			refund.setStatus(inputRefund.getStatus());
		if (inputRefund.getSanctionRef() != null)
			refund.setSanctionRef(inputRefund.getSanctionRef());
		if (inputRefund.getFinanceApprovalDate() != null)
			refund.setFinanceApprovalDate(inputRefund.getFinanceApprovalDate());
		if (inputRefund.getGatewayRefundId() != null)
			refund.setGatewayRefundId(inputRefund.getGatewayRefundId());

		// Complex Objects & Maps
		if (inputRefund.getBeneficiaryDetails() != null)
			refund.setBeneficiaryDetails(inputRefund.getBeneficiaryDetails());
		if (inputRefund.getAdditionalDetails() != null)
			refund.setAdditionalDetails(inputRefund.getAdditionalDetails());
		if (inputRefund.getFileStoreId() != null)
			refund.setFileStoreId(inputRefund.getFileStoreId());
	}

	// ============================================================
	// VALIDATION
	// ============================================================

	private void validateCreateRequest(Refund request) {

		if (request == null) {
			throw new IllegalArgumentException("Refund request cannot be null");
		}

		if (isBlank(request.getTenantId())) {
			throw new IllegalArgumentException("tenantId is mandatory");
		}

		if (isBlank(request.getModuleName())) {
			throw new IllegalArgumentException("moduleName is mandatory");
		}

		if (isBlank(request.getBusinessService())) {
			throw new IllegalArgumentException("businessService is mandatory");
		}

		validateAmounts(request.getAmountPaid(), request.getRefundAmount());
	}

	private void validateUpdateRequest(Refund refund) {

		if (refund == null) {
			throw new IllegalArgumentException("Refund is mandatory");
		}

		if (refund.getId() == null) {
			throw new IllegalArgumentException("Refund id is mandatory for update");
		}

		if (refund.getRefundAmount() == null) {
			throw new IllegalArgumentException("Refund amount is mandatory");
		}

		if (refund.getRefundAmount().compareTo(BigDecimal.ZERO) < 0) {

			throw new IllegalArgumentException("Refund amount cannot be negative");
		}

		if (isBlank(refund.getRefundCategory())) {
			throw new IllegalArgumentException("Refund category is mandatory");
		}

		if (isBlank(refund.getRefundReason())) {
			throw new IllegalArgumentException("Refund reason is mandatory");
		}
	}

	private void validateAmounts(BigDecimal amountPaid, BigDecimal refundAmount) {

		if (amountPaid == null || amountPaid.compareTo(BigDecimal.ZERO) < 0) {

			throw new IllegalArgumentException("amountPaid must be greater than or equal to zero");
		}

		if (refundAmount == null || refundAmount.compareTo(BigDecimal.ZERO) < 0) {

			throw new IllegalArgumentException("refundAmount must be greater than or equal to zero");
		}

		if (refundAmount.compareTo(amountPaid) > 0) {

			throw new IllegalArgumentException("refundAmount cannot be greater than amountPaid");
		}
	}

	private void validateActionRequest(RefundActionRequest request) {

		if (request == null) {
			throw new IllegalArgumentException("Refund action request cannot be null");
		}

		if (isBlank(request.getAction())) {
			throw new IllegalArgumentException("Action is mandatory");
		}

		if (isBlank(request.getUserId())) {
			throw new IllegalArgumentException("userId is mandatory");
		}
	}

	private void validateGetRequest(RefundGetRequest request) {

		if (request == null || request.getRequestInfo() == null) {

			throw new IllegalArgumentException("RequestInfo is mandatory");
		}

		if (isBlank(request.getId()) && isBlank(request.getRefundNo())) {

			throw new IllegalArgumentException("Either id or refundNo is mandatory");
		}
	}

	private void validateSearchRequest(RefundSearchRequest request) {

		if (request == null || request.getRequestInfo() == null) {

			throw new IllegalArgumentException("RequestInfo is mandatory");
		}
	}

	private void validateRequestInfo(RefundRequest request) {

		if (request == null || request.getRequestInfo() == null) {

			throw new IllegalArgumentException("RequestInfo is mandatory");
		}

		if (request.getRequestInfo().getUserInfo() == null) {
			throw new IllegalArgumentException("UserInfo is mandatory");
		}
	}

	// ============================================================
	// AUDIT
	// ============================================================

	private AuditDetails buildAuditDetails(String userId, long currentTime) {

		return AuditDetails.builder().createdBy(userId).createdTime(currentTime).lastModifiedBy(userId)
				.lastModifiedTime(currentTime).build();
	}

	private void updateAuditDetails(Refund refund, String userId, long currentTime) {

		if (refund.getAuditDetails() == null) {

			refund.setAuditDetails(buildAuditDetails(userId, currentTime));

			return;
		}

		// Do NOT modify createdBy / createdTime
		refund.getAuditDetails().setLastModifiedBy(userId);

		refund.getAuditDetails().setLastModifiedTime(currentTime);
	}

	// ============================================================
	// PAYMENT WORKFLOW
	// ============================================================

	private PaymentWorkflowRequest buildPaymentWorkflowRequest(Refund refund, RefundActionRequest actionRequest) {

		PaymentWorkflow paymentWorkflow = new PaymentWorkflow();

		paymentWorkflow.setPaymentId(refund.getPaymentId());

		paymentWorkflow.setAction(PaymentWorkflow.PaymentAction.REFUND);

		paymentWorkflow.setTenantId(refund.getTenantId());

		paymentWorkflow.setReason(refund.getRefundReason());

		paymentWorkflow.setAdditionalDetails(toJsonNode(refund.getAdditionalDetails()));

		PaymentWorkflowRequest request = new PaymentWorkflowRequest();

		request.setRequestInfo(actionRequest.getRequestInfo());

		request.setPaymentWorkflows(List.of(paymentWorkflow));

		return request;
	}

	// ============================================================
	// HELPERS
	// ============================================================

	private String resolveRefundMode(Refund refund) {

		if (!isBlank(refund.getRefundMode())) {
			return refund.getRefundMode();
		}

		if (RefundConstants.PAYMENT_MODE_ONLINE
				.equalsIgnoreCase(checkResolveRefundMode(refund.getPaymentModeOriginal()))) {

			return RefundConstants.PAYMENT_MODE_ONLINE;
		}

		if (RefundConstants.REFUND_MODE_OFFLINE
				.equalsIgnoreCase(checkResolveRefundMode(refund.getPaymentModeOriginal()))) {

			return RefundConstants.REFUND_MODE_OFFLINE;
		}

		return null;
	}

	private String checkResolveRefundMode(String paymentModeOriginal) {

		if (paymentModeOriginal == null) {
			throw new CustomException("INVALID_PAYMENT_MODE", "Original payment mode is required");
		}

		return switch (paymentModeOriginal.toUpperCase(Locale.ROOT)) {
		case RefundConstants.PAYMENT_MODE_ONLINE -> RefundConstants.PAYMENT_MODE_ONLINE;

		case RefundConstants.PAYMENT_MODE_CASH, RefundConstants.PAYMENT_MODE_DD, RefundConstants.PAYMENT_MODE_CHEQUE ->
			RefundConstants.REFUND_MODE_OFFLINE;

		default -> throw new CustomException("INVALID_PAYMENT_MODE",
				"Unsupported original payment mode: " + paymentModeOriginal);
		};
	}

	private String getUserId(RequestInfo requestInfo) {

		return requestInfo.getUserInfo().getUuid();
	}

	private BigDecimal defaultAmount(BigDecimal amount) {

		return amount != null ? amount : BigDecimal.ZERO;
	}

	private RefundResponse toResponse(Refund refund) {

		return RefundResponse.builder().refund(refund).build();
	}

	private boolean isBlank(String value) {
		return value == null || value.isBlank();
	}

	private JsonNode toJsonNode(Map<String, Object> map) {

		if (map == null) {
			return null;
		}

		return objectMapper.valueToTree(map);
	}

	private RequestInfo createSystemRequestInfo() {

		User userInfo = User.builder().uuid(applicationProperties.getSystemUUid()).type(RefundConstants.SYSTEM_USER)
				.roles(Collections
						.singletonList(Role.builder().code("SYSTEM").name("SYSTEM").tenantId("pg.citya").build()))
				.id(0L).build();

		return RequestInfo.builder().ver("1.0").ts(System.currentTimeMillis()).action("SYSTEM").did("1").key("")
				.msgId("refund-service|SYSTEM|" + UUID.randomUUID()).authToken(null).userInfo(userInfo).build();
	}
}