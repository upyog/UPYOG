package org.egov.services.refund;

import org.egov.commons.CVoucherHeader;
import org.egov.infra.config.core.ApplicationThreadLocals;
import org.egov.infstr.services.PersistenceService;
import org.egov.model.refund.RefundApplication;
import org.egov.pims.commons.Position;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

public class RefundApplicationService extends PersistenceService<RefundApplication, Long> {

	private static final String STATUS_REJECTED = "REJECTED";
	private static final String STATUS_APPROVED = "APPROVED";
	private static final String STATUS_PENDING_APPROVAL = "PENDING_APPROVAL";

	@Autowired
	private RefundJournalVoucherService refundJournalVoucherService;

	public RefundApplicationService() {
		super(RefundApplication.class);
	}

	public RefundApplicationService(final Class<RefundApplication> type) {
		super(type);
	}

	@Transactional
	public RefundApplication createRefundApplication(final RefundApplication refundApplication,
			final Position approverPosition) {

		validateRefundApplication(refundApplication, approverPosition);

		/*
		 * Use the authenticated caller for workflow audit fields. The approver's
		 * position identifies ownership, not the creator.
		 */
		final Long currentUserId = ApplicationThreadLocals.getUserId();

		if (currentUserId == null || currentUserId <= 0) {
			throw new IllegalArgumentException("Authenticated user ID is required " + "to create the refund workflow");
		}

		final RefundApplication existingApplication = find(
				"from RefundApplication " + "where tenantId=? " + "and refundApplicationNumber=?",
				refundApplication.getTenantId(), refundApplication.getRefundApplicationNumber());

		if (existingApplication != null) {
			throw new IllegalArgumentException(
					"Refund application already exists in Finance: " + refundApplication.getRefundApplicationNumber());
		}

		refundApplication.setStatus(STATUS_PENDING_APPROVAL);

		/*
		 * Assign an application ID before initializing its workflow. Both saves
		 * participate in the same transaction.
		 */
		persist(refundApplication);

		/*
		 * Start directly in the pending approval state. The existing Inbox includes
		 * STARTED workflows assigned to the approver's HRMS position.
		 *
		 * Explicitly populate workflow audit IDs because the new workflow state was
		 * otherwise inserted with createdby=null.
		 */
		refundApplication.transition().start().withOwner(approverPosition).withStateValue("Created")
				.withNextAction("Refund Approval").withNatureOfTask("Refund Approval")
				.withComments("Refund submitted for Finance approval").withCreatedBy(currentUserId)
				.withtLastModifiedBy(currentUserId);

		return persist(refundApplication);
	}

	private void validateRefundApplication(final RefundApplication refundApplication, final Position approverPosition) {

		if (refundApplication == null) {
			throw new IllegalArgumentException("Refund application is mandatory");
		}

		if (refundApplication.getTenantId() == null || refundApplication.getTenantId().trim().isEmpty()) {
			throw new IllegalArgumentException("Tenant ID is mandatory");
		}

		if (refundApplication.getRefundApplicationNumber() == null
				|| refundApplication.getRefundApplicationNumber().trim().isEmpty()) {
			throw new IllegalArgumentException("Refund application number is mandatory");
		}

		if (refundApplication.getModuleName() == null || refundApplication.getModuleName().trim().isEmpty()) {
			throw new IllegalArgumentException("Source module name is mandatory");
		}

		if (refundApplication.getBusinessService() == null || refundApplication.getBusinessService().trim().isEmpty()) {
			throw new IllegalArgumentException("Business service is mandatory");
		}

		if (refundApplication.getRefundAmount() == null || refundApplication.getRefundAmount().signum() <= 0) {
			throw new IllegalArgumentException("Refund amount must be greater than zero");
		}

		if (refundApplication.getDebitGlCode() == null || refundApplication.getDebitGlCode().trim().isEmpty()) {
			throw new IllegalArgumentException("Debit GL code is mandatory");
		}

		if (refundApplication.getCreditGlCode() == null || refundApplication.getCreditGlCode().trim().isEmpty()) {
			throw new IllegalArgumentException("Credit GL code is mandatory");
		}

		if (refundApplication.getFundCode() == null || refundApplication.getFundCode().trim().isEmpty()) {
			throw new IllegalArgumentException("Fund code is mandatory");
		}

		if (refundApplication.getDepartmentCode() == null || refundApplication.getDepartmentCode().trim().isEmpty()) {
			throw new IllegalArgumentException("Department code is mandatory");
		}

		if (approverPosition == null || approverPosition.getId() == null || approverPosition.getId() <= 0) {
			throw new IllegalArgumentException("Valid Finance approver position is mandatory");
		}
	}

	@Transactional
	public RefundApplication rejectRefundApplication(final RefundApplication refundApplication, final String comments) {

		validatePendingRefund(refundApplication);

		final Long currentUserId = getAuthenticatedWorkflowUserId();
		final String normalizedComments = comments == null ? "" : comments.trim();

		if (normalizedComments.isEmpty()) {
			throw new IllegalArgumentException("Comments are mandatory while rejecting a refund application");
		}

		if (normalizedComments.length() > 1000) {
			throw new IllegalArgumentException("Comments must not exceed 1000 characters");
		}

		refundApplication.transition().end().withStateValue("END").withNextAction("END")
				.withNatureOfTask("Refund Rejected").withComments(normalizedComments)
				.withtLastModifiedBy(currentUserId);

		refundApplication.setStatus(STATUS_REJECTED);
		refundApplication.setRejectionReason(normalizedComments);

		return persist(refundApplication);
	}

	@Transactional
	public RefundApplication approveRefundApplication(final RefundApplication refundApplication,
			final String comments) {

		validatePendingRefund(refundApplication);

		final Long currentUserId = getAuthenticatedWorkflowUserId();
		final String normalizedComments = comments == null ? "" : comments.trim();

		if (normalizedComments.length() > 1000) {
			throw new IllegalArgumentException("Comments must not exceed 1000 characters");
		}

		/*
		 * Create and forward the JV before completing refund approval. JV persistence
		 * must participate in this same transaction.
		 */
		final CVoucherHeader voucherHeader = refundJournalVoucherService.createJournalVoucher(refundApplication,
				normalizedComments);

		if (voucherHeader == null || voucherHeader.getVoucherNumber() == null
				|| voucherHeader.getVoucherNumber().trim().isEmpty()) {
			throw new IllegalArgumentException(
					"Journal Voucher creation failed for refund: " + refundApplication.getRefundApplicationNumber());
		}

		refundApplication.transition().end().withStateValue("END").withNextAction("END")
				.withNatureOfTask("Refund Approved").withComments(normalizedComments)
				.withtLastModifiedBy(currentUserId);

		refundApplication.setVoucherNumber(voucherHeader.getVoucherNumber());
		refundApplication.setStatus(STATUS_APPROVED);
		refundApplication.setRejectionReason(null);

		return persist(refundApplication);
	}

	private void validatePendingRefund(final RefundApplication refundApplication) {

		if (refundApplication == null || refundApplication.getId() == null) {
			throw new IllegalArgumentException("Refund application is required");
		}

		if (refundApplication.getState() == null) {
			throw new IllegalArgumentException("Refund application workflow state is required");
		}

		if (!STATUS_PENDING_APPROVAL.equals(refundApplication.getStatus())) {
			throw new IllegalArgumentException("Only pending refund applications can be approved or rejected");
		}

		if (refundApplication.getState().isEnded()) {
			throw new IllegalArgumentException("Refund application workflow has already ended");
		}

		if (!"Created".equals(refundApplication.getState().getValue())) {
			throw new IllegalArgumentException("Refund application is not in the approval workflow state");
		}

		if (refundApplication.getVoucherNumber() != null && !refundApplication.getVoucherNumber().trim().isEmpty()) {
			throw new IllegalArgumentException(
					"Journal Voucher is already created for refund: " + refundApplication.getRefundApplicationNumber());
		}
	}

	private Long getAuthenticatedWorkflowUserId() {

		final Long currentUserId = ApplicationThreadLocals.getUserId();

		if (currentUserId == null || currentUserId <= 0) {
			throw new IllegalArgumentException("Authenticated user ID is required for refund approval or rejection");
		}

		return currentUserId;
	}
}