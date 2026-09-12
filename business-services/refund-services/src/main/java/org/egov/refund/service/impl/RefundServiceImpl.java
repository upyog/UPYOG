package org.egov.refund.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.Role;
import org.egov.common.contract.request.User;
import org.egov.refund.Repository.RefundRepository;
import org.egov.refund.config.ApplicationProperties;
import org.egov.refund.model.Refund;
import org.egov.refund.model.WorkflowTransition;
import org.egov.refund.querybuilder.RefundSearchCriteria;
import org.egov.refund.service.FinanceService;
import org.egov.refund.service.PaymentRefundService;
import org.egov.refund.service.RefundAuditService;
import org.egov.refund.service.RefundEnrichmentService;
import org.egov.refund.service.RefundService;
import org.egov.refund.service.RefundValidator;
import org.egov.refund.service.WorkflowService;
import org.egov.refund.util.RefundConstants;
import org.egov.refund.web.contracat.RefundActionRequest;
import org.egov.refund.web.contracat.RefundGetRequest;
import org.egov.refund.web.contracat.RefundPaymentResponse;
import org.egov.refund.web.contracat.RefundRequest;
import org.egov.refund.web.contracat.RefundSearchRequest;
import org.egov.tracer.model.CustomException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RefundServiceImpl implements RefundService {

	private final ApplicationProperties applicationProperties;
	private final RefundRepository refundRepository;
	private final WorkflowService workflowService;
	private final FinanceService financeService;
	private final PaymentRefundService paymentRefundService;
	private final RefundAuditService refundAuditService;
	private final RefundValidator refundValidator;
	private final RefundEnrichmentService refundEnrichmentService;

	public RefundServiceImpl(ApplicationProperties applicationProperties, RefundRepository refundRepository,
			WorkflowService workflowService, FinanceService financeService, PaymentRefundService paymentRefundService,
			RefundAuditService refundAuditService, RefundValidator refundValidator,
			RefundEnrichmentService refundEnrichmentService) {

		this.applicationProperties = applicationProperties;
		this.refundRepository = refundRepository;
		this.workflowService = workflowService;
		this.financeService = financeService;
		this.paymentRefundService = paymentRefundService;
		this.refundAuditService = refundAuditService;
		this.refundValidator = refundValidator;
		this.refundEnrichmentService = refundEnrichmentService;
	}

	// ============================================================
	// CREATE
	// ============================================================

	@Override
	@Transactional
	public Refund create(RefundRequest request) {

		refundValidator.validateRequestInfo(request);
		refundValidator.validateCreateRequest(request.getRefund());
		refundEnrichmentService.enrichRefundPostValidate(request);
		Refund refund = request.getRefund();

		RefundActionRequest actionRequest = refundEnrichmentService.enrichWorkflowAction(request,
				refund.getProcessInstance().getAction());

		Refund processedRefund = processInternal(refund, actionRequest);

		refundRepository.save(processedRefund);

		return processedRefund;
	}

	// ============================================================
	// UPDATE
	// ============================================================

	@Override
	@Transactional
	public Refund update(RefundRequest request) {

		refundValidator.validateRequestInfo(request);
		refundValidator.validateUpdateRequest(request.getRefund());

		Refund inputRefund = request.getRefund();

		Refund refund = refundRepository.findById(inputRefund.getId());

		if (refund == null) {
			throw new IllegalArgumentException("Refund not found for id: " + inputRefund.getId());
		}

		/*
		 * Normal refund data update without workflow action.
		 */
		if (inputRefund.getProcessInstance() == null || isBlank(inputRefund.getProcessInstance().getAction())) {

			refundEnrichmentService.enrichRefundUpdate(refund, inputRefund);

			refundEnrichmentService.updateAuditDetails(refund, request.getRequestInfo().getUserInfo().getUuid(),
					System.currentTimeMillis());

			refundRepository.update(refund);

			return refund;
		}

		String action = inputRefund.getProcessInstance().getAction();

		if (RefundConstants.STATUS_PENDING_WITH_FINANCE.equalsIgnoreCase(refund.getStatus())) {

			return processFinanceAction(refund, action);
		}

		RefundActionRequest actionRequest = refundEnrichmentService.enrichWorkflowAction(request, action);
		refund = processInternal(refund, actionRequest);
		refundRepository.update(refund);
		return refund;
	}

	// ============================================================
	// GET
	// ============================================================

	@Override
	public Refund get(RefundGetRequest request) {

		refundValidator.validateGetRequest(request);

		Refund refund;

		if (!isBlank(request.getId())) {

			refund = refundRepository.findById(UUID.fromString(request.getId()));

		} else {

			refund = refundRepository.findByRefundNo(request.getRefundNo());
		}

		if (refund == null) {
			throw new IllegalArgumentException("Refund not found");
		}

		return refund;
	}

	// ============================================================
	// SEARCH
	// ============================================================

	@Override
	public List<Refund> search(RefundSearchRequest request) {

		refundValidator.validateSearchRequest(request);

		RefundSearchCriteria criteria = RefundSearchCriteria.builder().tenantId(request.getTenantId())
				.moduleName(request.getModuleName()).businessService(request.getBusinessService())
				.consumerCode(request.getConsumerCode()).paymentId(request.getPaymentId())
				.refundNo(request.getRefundNo()).status(request.getStatus()).refundCategory(request.getRefundCategory())
				.gatewayRefundId(request.getGatewayRefundId()).sanctionRef(request.getSanctionRef()).build();

		return refundRepository.search(criteria);
	}

	// ============================================================
	// PROCESS
	// ============================================================

	@Override
	@Transactional
	public Refund process(RefundActionRequest request) {

		refundValidator.validateActionRequest(request);

		Refund refund = refundRepository.findById(request.getId());

		if (refund == null) {
			throw new IllegalArgumentException("Refund not found for id: " + request.getId());
		}

		return processInternal(refund, request);
	}

	// ============================================================
	// MAIN WORKFLOW PROCESSING
	// ============================================================

	private Refund processInternal(Refund refund, RefundActionRequest request) {

		refundValidator.validateActionRequest(request);

		if (refund == null) {
			throw new IllegalArgumentException("Refund cannot be null");
		}

		WorkflowTransition transition = workflowService.validateAndGetNextState(refund, request);

		if (transition == null || !transition.isValid()) {

			throw new CustomException("INVALID_WORKFLOW",
					"Action " + request.getAction() + " is not allowed from status " + refund.getStatus());
		}

		refund.setStatus(transition.getApplicationStatus());

		refundEnrichmentService.updateAuditDetails(refund, request.getUserId(), System.currentTimeMillis());

		Refund processedRefund = processWorkflowAction(refund, transition, request);
		refundAuditService.createAudit(processedRefund, transition.getAction());
		return processedRefund;
	}

	// ============================================================
	// WORKFLOW ACTION HANDLER
	// ============================================================

	private Refund processWorkflowAction(Refund refund, WorkflowTransition transition, RefundActionRequest request) {

		String action = transition.getAction();

		if (RefundConstants.ACTION_APPROVE.equalsIgnoreCase(action)
				&& RefundConstants.STATUS_APPROVED.equalsIgnoreCase(transition.getApplicationStatus())) {

			return processApproval(refund);
		}

		if (RefundConstants.ACTION_CREATE_REQUEST.equalsIgnoreCase(action)) {

			return processFinanceRequest(refund);
		}

		if (RefundConstants.ACTION_REFUND_INITIATE.equalsIgnoreCase(action)) {

			return processRefundBasedOnMode(refund, request);
		}

		return refund;
	}

	// ============================================================
	// APPROVAL PROCESSING
	// ============================================================

	private Refund processApproval(Refund refund) {

		if (refund == null) {
			throw new IllegalArgumentException("Refund cannot be null for approval processing");
		}

		RequestInfo systemRequestInfo = createSystemRequestInfo();

		String nextAction = applicationProperties.isSendToFinance() ? RefundConstants.ACTION_CREATE_REQUEST
				: RefundConstants.ACTION_REFUND_INITIATE;

		RefundActionRequest nextActionRequest = RefundActionRequest.builder().id(refund.getId()).action(nextAction)
				.userId(systemRequestInfo.getUserInfo().getUuid()).requestInfo(systemRequestInfo).build();

		return processInternal(refund, nextActionRequest);
	}

	// ============================================================
	// FINANCE REQUEST
	// ============================================================

	private Refund processFinanceRequest(Refund refund) {

		RefundRequest refundRequest = RefundRequest.builder().refund(refund).requestInfo(createSystemRequestInfo())
				.build();

		financeService.processRefund(refundRequest);

		return refund;
	}

	// ============================================================
	// FINANCE APPROVE / REJECT
	// ============================================================

	private Refund processFinanceAction(Refund refund, String action) {

		if (refund == null) {
			throw new IllegalArgumentException("Refund cannot be null");
		}

		if (isBlank(action)) {
			throw new IllegalArgumentException("Finance action cannot be blank");
		}

		if (!RefundConstants.ACTION_APPROVE.equalsIgnoreCase(action)
				&& !RefundConstants.ACTION_REJECT.equalsIgnoreCase(action)) {

			throw new IllegalArgumentException("Unsupported finance action: " + action);
		}

		RequestInfo systemRequestInfo = createSystemRequestInfo();

		RefundActionRequest actionRequest = RefundActionRequest.builder().id(refund.getId()).action(action)
				.userId(systemRequestInfo.getUserInfo().getUuid()).requestInfo(systemRequestInfo).build();

		return processInternal(refund, actionRequest);
	}

	// ============================================================
	// REFUND PROCESSING
	// ============================================================

	private Refund processRefundBasedOnMode(Refund refund, RefundActionRequest request) {

		String refundMode = refund.getRefundMode();

		if (RefundConstants.PAYMENT_MODE_ONLINE.equalsIgnoreCase(refundMode)) {

			refundEnrichmentService.updateAuditDetails(refund, request.getUserId(), System.currentTimeMillis());

			RefundPaymentResponse refundResponse = paymentRefundService.initiateRefund(refund,
					request.getRequestInfo());
			refund.setGatewayRefundId(refundResponse.getPaymentRefund().getRefundId());
			return refund;
		}

		if (RefundConstants.REFUND_MODE_OFFLINE.equalsIgnoreCase(refundMode)) {

			refundEnrichmentService.updateAuditDetails(refund, request.getUserId(), System.currentTimeMillis());

			return refund;
		}

		throw new IllegalArgumentException("Invalid refund mode: " + refundMode);
	}

	// ============================================================
	// SYSTEM REQUEST INFO
	// ============================================================

	private RequestInfo createSystemRequestInfo() {

		User systemUser = User.builder().uuid(applicationProperties.getSystemUUid()).type("SYSTEM")
				.roles(Collections.singletonList(Role.builder().code("SYSTEM").name("SYSTEM").build())).build();

		return RequestInfo.builder().apiId("refund-service").ver("1.0").ts(System.currentTimeMillis())
				.msgId(UUID.randomUUID().toString()).userInfo(systemUser).build();
	}

	// ============================================================
	// UTILITY
	// ============================================================

	private boolean isBlank(String value) {
		return value == null || value.trim().isEmpty();
	}
}
