package org.egov.refund.service.impl;

import java.util.UUID;

import org.egov.refund.Repository.RefundAuditRepository;
import org.egov.refund.model.Refund;
import org.egov.refund.model.RefundAudit;
import org.egov.refund.service.RefundAuditService;
import org.springframework.stereotype.Service;

@Service
public class RefundAuditServiceImpl implements RefundAuditService {

	private final RefundAuditRepository refundAuditRepository;

	public RefundAuditServiceImpl(RefundAuditRepository refundAuditRepository) {
		this.refundAuditRepository = refundAuditRepository;
	}

	@Override
	public void createAudit(Refund refund, String action) {

		if (refund == null) {
			return;
		}

		RefundAudit audit = RefundAudit.builder().auditId(UUID.randomUUID()).id(refund.getId())
				.refundNo(refund.getRefundNo()).tenantId(refund.getTenantId()).moduleName(refund.getModuleName())
				.businessService(refund.getBusinessService()).consumerCode(refund.getConsumerCode())
				.paymentId(refund.getPaymentId()).applicantName(refund.getApplicantName())
				.mobileNumber(refund.getMobileNumber()).refundCategory(refund.getRefundCategory())
				.refundReason(refund.getRefundReason()).paymentModeOriginal(refund.getPaymentModeOriginal())
				.amountPaid(refund.getAmountPaid()).refundAmount(refund.getRefundAmount())
				.refundMode(refund.getRefundMode()).status(refund.getStatus()).sanctionRef(refund.getSanctionRef())
				.financeApprovalDate(refund.getFinanceApprovalDate()).gatewayRefundId(refund.getGatewayRefundId())
				.fileStoreId(refund.getFileStoreId()).beneficiaryDetails(refund.getBeneficiaryDetails())
				.additionalDetails(refund.getAdditionalDetails()).auditDetails(refund.getAuditDetails())
				.auditCreatedTime(System.currentTimeMillis()).action(action).build();

		refundAuditRepository.create(audit);
	}
}