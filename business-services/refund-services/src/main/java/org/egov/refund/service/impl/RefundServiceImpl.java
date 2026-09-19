package org.egov.refund.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.Role;
import org.egov.common.contract.request.User;
import org.egov.refund.Repository.RefundRepository;
import org.egov.refund.config.ApplicationProperties;
import org.egov.refund.model.PaymentRefund;
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

import lombok.extern.slf4j.Slf4j;

@Slf4j
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
	public Refund create(RefundRequest request) {

		log.info("Refund create request received");
		refundValidator.validateRequestInfo(request);
		refundValidator.validateCreateRequest(request.getRefund());
		refundEnrichmentService.enrichRefundPostValidate(request);
		Refund refund = request.getRefund();

		log.info("Creating refund. id={}, tenantId={}, consumerCode={}, paymentId={}", refund.getId(),
				refund.getTenantId(), refund.getConsumerCode(), refund.getPaymentId());

		RefundActionRequest actionRequest = refundEnrichmentService.enrichWorkflowAction(refund,
				request.getRequestInfo(), refund.getProcessInstance().getAction());

		log.info("Processing refund workflow. id={}, action={}, status={}", refund.getId(), actionRequest.getAction(),
				refund.getStatus());
		Refund processedRefund = processInternal(refund, actionRequest);

		refundRepository.save(processedRefund);

		log.info("Refund created successfully. id={}, refundNo={}, status={}", processedRefund.getId(),
				processedRefund.getRefundNo(), processedRefund.getStatus());

		return processedRefund;
	}

	// ============================================================
	// UPDATE
	// ============================================================

	@Override
	public Refund update(RefundRequest request) {

		log.info("Refund update request received");

		refundValidator.validateRequestInfo(request);
		refundValidator.validateUpdateRequest(request.getRefund());

		Refund inputRefund = request.getRefund();
		log.info("Updating refund. refundId={}, tenantId={}, refundNo={}", inputRefund.getId(),
				inputRefund.getTenantId(), inputRefund.getRefundNo());

		Refund refund = refundRepository.findById(inputRefund.getId());

		if (refund == null) {
			throw new IllegalArgumentException("Refund not found for id: " + inputRefund.getId());
		}

		log.info("Existing refund found. refundId={}, currentStatus={}", refund.getId(), refund.getStatus());

		/*
		 * Normal refund data update without workflow action.
		 */

		if (inputRefund.getProcessInstance() == null || isBlank(inputRefund.getProcessInstance().getAction())) {
			log.info("Processing normal refund data update. refundId={}", refund.getId());

			refundEnrichmentService.enrichRefundUpdate(refund, inputRefund);

			refundEnrichmentService.updateAuditDetails(refund, request.getRequestInfo().getUserInfo().getUuid());

			refundRepository.update(refund);

			log.info("Refund data updated successfully. refundId={}, status={}", refund.getId(), refund.getStatus());
			return refund;
		}

		String action = inputRefund.getProcessInstance().getAction();

		log.info("Workflow update requested. refundId={}, currentStatus={}, action={}", refund.getId(),
				refund.getStatus(), action);

		if (RefundConstants.STATUS_PENDING_WITH_FINANCE.equalsIgnoreCase(refund.getStatus())) {

			refund = processFinanceAction(refund, action);
			refundRepository.update(refund);
			return refund;
		}

		RefundActionRequest actionRequest = refundEnrichmentService.enrichWorkflowAction(refund,
				request.getRequestInfo(), action);

		log.info("Processing workflow action. refundId={}, action={}", refund.getId(), action);

		refund = processInternal(refund, actionRequest);
		refundRepository.update(refund);

		log.info("Refund workflow update completed. refundId={}, action={}, finalStatus={}", refund.getId(), action,
				refund.getStatus());
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

			refund = refundRepository.findById(request.getId());

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
	// MAIN WORKFLOW PROCESSING
	// ============================================================

	private Refund processInternal(Refund refund, RefundActionRequest request) {

		log.info("Workflow processing started. refundId={}, action={}, currentStatus={}", refund.getId(),
				request.getAction(), refund.getStatus());

		refundValidator.validateActionRequest(request);

		WorkflowTransition transition = workflowService.validateAndGetNextState(refund, request);

		if (transition == null || !transition.isValid()) {

			log.error("Invalid refund workflow transition. refundId={}, action={}, status={}", refund.getId(),
					request.getAction(), refund.getStatus());

			throw new CustomException("INVALID_WORKFLOW",
					"Action " + request.getAction() + " is not allowed from status " + refund.getStatus());
		}

		log.info("Workflow transition successful. refundId={}, action={}, oldStatus={}, newStatus={}", refund.getId(),
				transition.getAction(), refund.getStatus(), transition.getApplicationStatus());

		refund.setStatus(transition.getApplicationStatus());

		refundEnrichmentService.updateAuditDetails(refund, request.getUserId());
		if (!RefundConstants.ACTION_INITIATE.equalsIgnoreCase(transition.getAction())) {

			refundAuditService.createAudit(refund, transition.getAction());
		}
		Refund processedRefund = processWorkflowAction(refund, transition, request);

		log.info("Workflow processing completed. refundId={}, status={}", processedRefund.getId(),
				processedRefund.getStatus());

		return processedRefund;
	}

	// ============================================================
	// WORKFLOW ACTION HANDLER
	// ============================================================

	private Refund processWorkflowAction(Refund refund, WorkflowTransition transition, RefundActionRequest request) {

		String action = transition.getAction();
		String applicationStatus = transition.getApplicationStatus();

		if (RefundConstants.ACTION_APPROVE.equalsIgnoreCase(action)
				&& RefundConstants.STATUS_APPROVED.equalsIgnoreCase(transition.getApplicationStatus())) {

			return processApproval(refund);
		}

		if (RefundConstants.ACTION_CREATE_REQUEST.equalsIgnoreCase(action)) {

			return processFinanceRequest(refund);
		}

		if (RefundConstants.ACTION_REFUND_INITIATE.equalsIgnoreCase(action)
				|| RefundConstants.STATUS_REFUND_INITIATED.equalsIgnoreCase(applicationStatus)) {

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

		RefundActionRequest actionRequest = refundEnrichmentService.enrichWorkflowAction(refund,
				createSystemRequestInfo(), action);

		Refund processedRefund = processInternal(refund, actionRequest);

		return processedRefund;
	}

	// ============================================================
	// REFUND PROCESSING
	// ============================================================

	private Refund processRefundBasedOnMode(Refund refund, RefundActionRequest request) {

		String refundMode = refund.getRefundMode();

		if (RefundConstants.PAYMENT_MODE_ONLINE.equalsIgnoreCase(refundMode)) {

			refundEnrichmentService.updateAuditDetails(refund, request.getUserId());

			RefundPaymentResponse refundResponse = paymentRefundService.initiateRefund(refund,
					request.getRequestInfo());
			refund.setGatewayRefundId(refundResponse.getPaymentRefund().getRefundId());
			return refund;
		}

		if (RefundConstants.REFUND_MODE_OFFLINE.equalsIgnoreCase(refundMode)) {

			refundEnrichmentService.updateAuditDetails(refund, request.getUserId());

			return refund;
		}

		throw new IllegalArgumentException("Invalid refund mode: " + refundMode);
	}

	@Override
	public void processPaymentRefund(PaymentRefund paymentRefund) {

		log.info("Payment refund response received. gatewayRefundId={}, tenantId={}, status={}",
				paymentRefund.getRefundId(), paymentRefund.getTenantId(), paymentRefund.getStatus());

		String status = paymentRefund.getStatus();
		String message = paymentRefund.getGatewayStatusMsg();

		if (isFinalPaymentRefundStatus(status, message)) {
			// refundId from PaymentRefund represents gateway refund ID
			Refund refund = refundRepository.findByGatwayRefundId(paymentRefund.getRefundId(),
					paymentRefund.getTenantId());

			if (refund == null) {
				throw new IllegalStateException("Refund not found for gatewayRefundId: " + paymentRefund.getRefundId());
			}

			refundRepository.update(refund);

			String action;

			if (RefundConstants.PAYMENT_REFUND_STATUS_SUCCESS.equalsIgnoreCase(status)) {

				action = RefundConstants.ACTION_REFUND_COMPLETED;

			} else {

				action = RefundConstants.ACTION_REJECT;
			}

			log.info("Final payment refund response. refundId={}, paymentStatus={}, workflowAction={}", refund.getId(),
					status, action);

			RefundActionRequest actionRequest = refundEnrichmentService.enrichWorkflowAction(refund,
					createSystemRequestInfo(), action);

			// Process workflow only for SUCCESS / FAILURE
			refund = processInternal(refund, actionRequest);

			// Save gateway response + final workflow status
			refundRepository.update(refund);

			log.info("Payment refund processing completed. refundId={}, finalStatus={}", refund.getId(),
					refund.getStatus());
		}
	}

	private boolean isFinalPaymentRefundStatus(String status, String message) {

		return (RefundConstants.PAYMENT_REFUND_STATUS_SUCCESS.equalsIgnoreCase(status)
				&& RefundConstants.PAYMENT_REFUND_STATUS_SUCCESS.equalsIgnoreCase(message))

				|| RefundConstants.PAYMENT_REFUND_STATUS_FAILURE.equalsIgnoreCase(status)

				|| RefundConstants.PAYMENT_REFUND_STATUS_FAILED.equalsIgnoreCase(status);
	}

	// ============================================================
	// SYSTEM REQUEST INFO
	// ============================================================

	private RequestInfo createSystemRequestInfo() {

		User systemUser = User.builder().uuid(applicationProperties.getSystemUUid()).type(RefundConstants.SYSTEM)
				.roles(Collections.singletonList(Role.builder().code(RefundConstants.SYSTEM)
						.name(RefundConstants.SYSTEM).tenantId(applicationProperties.getStateLevelTenantId()).build()))
				.build();

		return RequestInfo.builder().apiId(applicationProperties.getApplicationName()).ver("1.0")
				.ts(System.currentTimeMillis()).msgId(UUID.randomUUID().toString()).userInfo(systemUser).build();
	}

	// ============================================================
	// UTILITY
	// ============================================================

	private boolean isBlank(String value) {
		return value == null || value.trim().isEmpty();
	}
}
