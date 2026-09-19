package org.egov.refund.service.impl;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.egov.common.contract.idgen.IdResponse;
import org.egov.common.contract.request.RequestInfo;
import org.egov.refund.Repository.IdGenRepository;
import org.egov.refund.config.ApplicationProperties;
import org.egov.refund.model.AuditDetails;
import org.egov.refund.model.Refund;
import org.egov.refund.service.RefundEnrichmentService;
import org.egov.refund.util.RefundConstants;
import org.egov.refund.web.contracat.RefundActionRequest;
import org.egov.refund.web.contracat.RefundRequest;
import org.egov.tracer.model.CustomException;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
public class RefundEnrichmentServiceImpl implements RefundEnrichmentService {

	private final IdGenRepository idGenRepository;
	private final String idKey;
	private final String idformat;

	public RefundEnrichmentServiceImpl(IdGenRepository idGenRepository, ApplicationProperties properties) {
		this.idGenRepository = idGenRepository;
		this.idKey = properties.getRefundIdgenName();
		this.idformat = properties.getRefundIdgenFormat();
	}

	// ============================================================
	// CREATE ENRICHMENT
	// ============================================================

	@Override
	public void enrichRefundPostValidate(RefundRequest request) {

		if (request == null) {
			throw new IllegalArgumentException("Refund request cannot be null");
		}

		if (request.getRefund() == null) {
			throw new IllegalArgumentException("Refund cannot be null");
		}

		if (request.getRequestInfo() == null || request.getRequestInfo().getUserInfo() == null) {

			throw new IllegalArgumentException("RequestInfo/UserInfo is mandatory");
		}

		Refund refund = request.getRefund();
		RequestInfo requestInfo = request.getRequestInfo();

		String userId = requestInfo.getUserInfo().getUuid();

		refund.setId(UUID.randomUUID().toString());

		String refundNo = getId(requestInfo, refund.getTenantId(), idKey, idformat);

		refund.setRefundNo(refundNo);

		refund.setAmountPaid(defaultAmount(refund.getAmountPaid()));

		refund.setRefundAmount(defaultAmount(refund.getRefundAmount()));

		refund.setRefundMode(resolveRefundMode(refund));

		refund.setAuditDetails(buildAuditDetails(userId));
	}

	// ============================================================
	// UPDATE ENRICHMENT
	// ============================================================

	@Override
	public void enrichRefundUpdate(Refund existingRefund, Refund inputRefund) {

		if (existingRefund == null) {
			throw new IllegalArgumentException("Existing refund cannot be null");
		}

		if (inputRefund == null) {
			throw new IllegalArgumentException("Input refund cannot be null");
		}

		if (inputRefund.getPaymentId() != null) {
			existingRefund.setPaymentId(inputRefund.getPaymentId());
		}

		if (inputRefund.getPaymentModeOriginal() != null) {
			existingRefund.setPaymentModeOriginal(inputRefund.getPaymentModeOriginal());
		}

		if (inputRefund.getApplicantName() != null) {
			existingRefund.setApplicantName(inputRefund.getApplicantName());
		}

		if (inputRefund.getMobileNumber() != null) {
			existingRefund.setMobileNumber(inputRefund.getMobileNumber());
		}

		if (inputRefund.getRefundCategory() != null) {
			existingRefund.setRefundCategory(inputRefund.getRefundCategory());
		}

		if (inputRefund.getRefundReason() != null) {
			existingRefund.setRefundReason(inputRefund.getRefundReason());
		}

		if (inputRefund.getAmountPaid() != null) {
			existingRefund.setAmountPaid(inputRefund.getAmountPaid());
		}

		if (inputRefund.getRefundAmount() != null) {
			existingRefund.setRefundAmount(inputRefund.getRefundAmount());
		}

		if (inputRefund.getRefundMode() != null || inputRefund.getPaymentModeOriginal() != null) {

			existingRefund.setRefundMode(resolveRefundMode(inputRefund));
		}

		if (inputRefund.getSanctionRef() != null) {
			existingRefund.setSanctionRef(inputRefund.getSanctionRef());
		}

		if (inputRefund.getFinanceApprovalDate() != null) {
			existingRefund.setFinanceApprovalDate(inputRefund.getFinanceApprovalDate());
		}

		if (inputRefund.getGatewayRefundId() != null) {
			existingRefund.setGatewayRefundId(inputRefund.getGatewayRefundId());
		}

		if (inputRefund.getFileStoreId() != null) {
			existingRefund.setFileStoreId(inputRefund.getFileStoreId());
		}

		if (inputRefund.getBeneficiaryDetails() != null) {
			existingRefund.setBeneficiaryDetails(inputRefund.getBeneficiaryDetails());
		}

		if (inputRefund.getAdditionalDetails() != null) {
			existingRefund.setAdditionalDetails(inputRefund.getAdditionalDetails());
		}
	}

	// ============================================================
	// WORKFLOW ACTION ENRICHMENT
	// ============================================================

	@Override
	public RefundActionRequest enrichWorkflowAction(Refund refund, RequestInfo requestInfo, String action) {

		if (requestInfo == null || requestInfo.getUserInfo() == null) {

			throw new IllegalArgumentException("RequestInfo/UserInfo is mandatory");
		}

		if (isBlank(action)) {
			throw new IllegalArgumentException("Workflow action is mandatory");
		}

		String userId = requestInfo.getUserInfo().getUuid();

		return RefundActionRequest.builder().id(refund != null ? refund.getId() : null).action(action).userId(userId)
				.requestInfo(requestInfo).build();
	}

	// ============================================================
	// AUDIT DETAILS
	// ============================================================

	@Override
	public void updateAuditDetails(Refund refund, String userId) {

		if (refund == null) {
			throw new IllegalArgumentException("Refund cannot be null");
		}

		if (isBlank(userId)) {
			throw new IllegalArgumentException("userId is mandatory for audit");
		}

		if (refund.getAuditDetails() == null) {

			refund.setAuditDetails(buildAuditDetails(userId));

			return;
		}

		refund.getAuditDetails().setLastModifiedBy(userId);

		refund.getAuditDetails().setLastModifiedTime(getCurrentTimestamp());
	}

	// ============================================================
	// BUILD AUDIT DETAILS
	// ============================================================

	private AuditDetails buildAuditDetails(String userId) {
		return AuditDetails.builder().createdBy(userId).createdTime(getCurrentTimestamp()).lastModifiedBy(userId)
				.lastModifiedTime(getCurrentTimestamp()).build();
	}

	// ============================================================
	// REFUND MODE
	// ============================================================

	private String resolveRefundMode(Refund refund) {

		if (!isBlank(refund.getRefundMode())) {

			return checkResolveRefundMode(refund.getRefundMode(), true);
		}

		return checkResolveRefundMode(refund.getPaymentModeOriginal(), false);
	}

	private String checkResolveRefundMode(String mode, boolean refundMode) {

		if (isBlank(mode)) {

			if (refundMode) {
				throw new CustomException("INVALID_REFUND_MODE", "Refund mode is required");
			}

			throw new CustomException("INVALID_PAYMENT_MODE", "Original payment mode is required");
		}

		String normalized = mode.trim().toUpperCase(Locale.ROOT);

		if (refundMode) {

			return switch (normalized) {

			case RefundConstants.PAYMENT_MODE_ONLINE -> RefundConstants.PAYMENT_MODE_ONLINE;

			case RefundConstants.REFUND_MODE_OFFLINE -> RefundConstants.REFUND_MODE_OFFLINE;

			default -> throw new CustomException("INVALID_REFUND_MODE", "Unsupported refund mode: " + mode);
			};
		}

		/*
		 * -------------------------------------------------------- Original payment
		 * mode --------------------------------------------------------
		 */
		return switch (normalized) {

		case RefundConstants.PAYMENT_MODE_ONLINE -> RefundConstants.PAYMENT_MODE_ONLINE;

		case RefundConstants.PAYMENT_MODE_CASH, RefundConstants.PAYMENT_MODE_DD, RefundConstants.PAYMENT_MODE_CHEQUE ->
			RefundConstants.REFUND_MODE_OFFLINE;

		default -> throw new CustomException("INVALID_PAYMENT_MODE", "Unsupported original payment mode: " + mode);
		};
	}

	// ============================================================
	// AMOUNT
	// ============================================================

	private BigDecimal defaultAmount(BigDecimal amount) {

		return amount != null ? amount : BigDecimal.ZERO;
	}

	// ============================================================
	// UTILITY
	// ============================================================

	private boolean isBlank(String value) {

		return value == null || value.trim().isEmpty();
	}

	private String getId(RequestInfo requestInfo, String tenantId, String idKey, String idformat) {
		List<IdResponse> idResponses = idGenRepository.getId(requestInfo, tenantId, idKey, idformat).getIdResponses();

		if (CollectionUtils.isEmpty(idResponses))
			throw new CustomException("IDGEN ERROR", "No ids returned from idgen Service");

		return idResponses.get(0).getId();
	}

	public static Long getCurrentTimestamp() {
		return Instant.now().toEpochMilli();
	}

}