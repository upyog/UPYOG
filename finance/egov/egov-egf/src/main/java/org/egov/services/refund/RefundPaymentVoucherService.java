package org.egov.services.refund;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;

import org.apache.commons.lang.StringUtils;
import org.egov.billsaccounting.services.VoucherConstant;
import org.egov.commons.Bankaccount;
import org.egov.commons.CVoucherHeader;
import org.egov.egf.contract.model.RefundPaymentCreateRequest;
import org.egov.egf.contract.model.RefundPaymentDetail;
import org.egov.infstr.services.PersistenceService;
import org.egov.model.payment.Paymentheader;
import org.egov.model.refund.RefundApplication;
import org.egov.services.payment.PaymentService;
import org.egov.utils.FinancialConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

public class RefundPaymentVoucherService {

	private static final String REFUND_APPROVED = "APPROVED";

	private static final String PAYMENT_VOUCHER_CREATED = "PAYMENT_VOUCHER_CREATED";

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	private PaymentService paymentService;

	@Autowired
	@Qualifier("persistenceService")
	private PersistenceService persistenceService;

	/*
	 * Keep the gateway success status configurable until the exact value from
	 * refund-service is finalized.
	 */
	@Value("${egov.refund.payment.success.status:REFUND_COMPLETED}")
	private String successfulPaymentStatus;

	@Transactional
	public RefundApplication createPaymentVoucher(final RefundPaymentCreateRequest request) {

		validateRequest(request);

		final RefundPaymentDetail payment = request.getRefundPayment();

		/*
		 * A pessimistic lock prevents two concurrent Kafka deliveries from creating two
		 * Payment Vouchers for the same refund.
		 */
		final RefundApplication refundApplication = findAndLockRefundApplication(request.getTenantId(),
				payment.getRefundApplicationNumber());

		validateRefundForPayment(refundApplication, payment);

		if (StringUtils.isNotBlank(refundApplication.getPaymentVoucherNumber())) {

			/*
			 * Kafka may deliver the same refund-completion event more than once. Return the
			 * existing result without creating another Payment Voucher.
			 */
			return refundApplication;
		}

		if (StringUtils.isNotBlank(payment.getGatewayRefundId())) {

			validateGatewayRefundIdIsUnique(request.getTenantId(), payment.getGatewayRefundId().trim(),
					refundApplication.getId());
		}

		final Bankaccount bankAccount = findBankAccount(payment.getBankAccountNumber());

		final Paymentheader paymentHeader = createApprovedPaymentVoucher(refundApplication, payment, bankAccount);

		final CVoucherHeader voucherHeader = paymentHeader.getVoucherheader();

		if (voucherHeader == null || StringUtils.isBlank(voucherHeader.getVoucherNumber())) {

			throw new IllegalArgumentException("Refund Payment Voucher creation failed");
		}

		if (StringUtils.isNotBlank(payment.getGatewayRefundId())) {
			refundApplication.setGatewayRefundId(payment.getGatewayRefundId().trim());
		}

		if (StringUtils.isNotBlank(payment.getGatewayTransactionId())) {

			refundApplication.setGatewayTransactionId(payment.getGatewayTransactionId().trim());
		}

		if (payment.getGatewayTransactionDate() != null && payment.getGatewayTransactionDate() > 0) {

			refundApplication.setGatewayTransactionDate(payment.getGatewayTransactionDate());
		}
		refundApplication.setRefundPaymentMode(payment.getRefundMode());

		refundApplication.setRefundPaymentStatus(PAYMENT_VOUCHER_CREATED);

		refundApplication.setPaymentVoucherNumber(voucherHeader.getVoucherNumber());

		refundApplication.setPaymentProcessedDate(System.currentTimeMillis());

		entityManager.merge(refundApplication);
		entityManager.flush();

		return refundApplication;
	}

	private RefundApplication findAndLockRefundApplication(final String tenantId,
			final String refundApplicationNumber) {

		final List<RefundApplication> applications = entityManager
				.createQuery("from RefundApplication " + "where tenantId = :tenantId " + "and refundApplicationNumber "
						+ "= :refundApplicationNumber", RefundApplication.class)
				.setParameter("tenantId", tenantId).setParameter("refundApplicationNumber", refundApplicationNumber)
				.setLockMode(LockModeType.PESSIMISTIC_WRITE).setMaxResults(1).getResultList();

		if (applications.isEmpty()) {
			throw new IllegalArgumentException(
					"Refund application was not found in Finance: " + refundApplicationNumber);
		}

		return applications.get(0);
	}

	private void validateRequest(final RefundPaymentCreateRequest request) {

		if (request == null) {
			throw new IllegalArgumentException("Refund payment request is mandatory");
		}

		if (request.getRequestInfo() == null) {
			throw new IllegalArgumentException("RequestInfo is mandatory");
		}

		if (StringUtils.isBlank(request.getTenantId())) {
			throw new IllegalArgumentException("Tenant ID is mandatory");
		}

		if (request.getRefundPayment() == null) {
			throw new IllegalArgumentException("RefundPayment details are mandatory");
		}

		final RefundPaymentDetail payment = request.getRefundPayment();

		if (StringUtils.isBlank(payment.getRefundApplicationNumber())) {

			throw new IllegalArgumentException("Refund application number is mandatory");
		}

		if (payment.getRefundAmount() == null || payment.getRefundAmount().signum() <= 0) {

			throw new IllegalArgumentException("Refund amount must be greater than zero");
		}

		if (StringUtils.isBlank(payment.getPaymentStatus())) {
			throw new IllegalArgumentException("Payment status is mandatory");
		}

		if (!successfulPaymentStatus.equalsIgnoreCase(payment.getPaymentStatus())) {

			throw new IllegalArgumentException(
					"Payment Voucher can be created only for " + successfulPaymentStatus + " status");
		}

		if (StringUtils.isBlank(payment.getRefundMode())) {
			throw new IllegalArgumentException("Refund mode is mandatory");
		}

		if (StringUtils.isBlank(payment.getPayableGlCode())) {
			throw new IllegalArgumentException("Refund payable GL code is mandatory");
		}

		if (StringUtils.isBlank(payment.getBankAccountNumber())) {

			throw new IllegalArgumentException("Finance bank account number is mandatory");
		}

		if (StringUtils.isBlank(payment.getFundCode())) {
			throw new IllegalArgumentException("Fund code is mandatory");
		}

		if (StringUtils.isBlank(payment.getDepartmentCode())) {

			throw new IllegalArgumentException("Department code is mandatory");
		}
	}

	private void validateRefundForPayment(final RefundApplication refundApplication,
			final RefundPaymentDetail payment) {

		if (!REFUND_APPROVED.equalsIgnoreCase(refundApplication.getStatus())) {

			throw new IllegalArgumentException(
					"Refund must be approved in Finance before " + "creating its Payment Voucher");
		}

		if (StringUtils.isBlank(refundApplication.getVoucherNumber())) {

			throw new IllegalArgumentException("Approved refund Journal Voucher is missing");
		}

		if (refundApplication.getRefundAmount() == null
				|| refundApplication.getRefundAmount().compareTo(payment.getRefundAmount()) != 0) {

			throw new IllegalArgumentException(
					"Refund payment amount does not match " + "the Finance-approved refund amount");
		}
	}

	private void validateGatewayRefundIdIsUnique(final String tenantId, final String gatewayRefundId,
			final Long currentRefundId) {

		final List<RefundApplication> applications = entityManager
				.createQuery(
						"from RefundApplication " + "where tenantId = :tenantId "
								+ "and gatewayRefundId = :gatewayRefundId " + "and id <> :currentRefundId",
						RefundApplication.class)
				.setParameter("tenantId", tenantId).setParameter("gatewayRefundId", gatewayRefundId)
				.setParameter("currentRefundId", currentRefundId).setMaxResults(1).getResultList();

		if (!applications.isEmpty()) {
			throw new IllegalArgumentException("Gateway refund ID was already processed: " + gatewayRefundId);
		}
	}

	@SuppressWarnings("unchecked")
	private Bankaccount findBankAccount(final String bankAccountNumber) {

		final List<Bankaccount> bankAccounts = persistenceService
				.findAllBy("from Bankaccount " + "where accountNumber = ?", bankAccountNumber);

		if (bankAccounts == null || bankAccounts.isEmpty()) {

			throw new IllegalArgumentException("Finance bank account was not found: " + bankAccountNumber);
		}

		if (bankAccounts.size() > 1) {
			throw new IllegalArgumentException(
					"Multiple Finance bank accounts were found " + "for account number: " + bankAccountNumber);
		}

		final Bankaccount bankAccount = bankAccounts.get(0);

		if (bankAccount.getChartofaccounts() == null
				|| StringUtils.isBlank(bankAccount.getChartofaccounts().getGlcode())) {

			throw new IllegalArgumentException(
					"GL code is not configured for Finance " + "bank account: " + bankAccountNumber);
		}

		return bankAccount;
	}

	private Paymentheader createApprovedPaymentVoucher(final RefundApplication refundApplication,
			final RefundPaymentDetail payment, final Bankaccount bankAccount) {

		final HashMap<String, Object> headerDetails = new HashMap<>();

		final Date paymentDate = payment.getGatewayTransactionDate() != null && payment.getGatewayTransactionDate() > 0
				? new Date(payment.getGatewayTransactionDate())
				: new Date();

		headerDetails.put(VoucherConstant.VOUCHERDATE, paymentDate);

		headerDetails.put(VoucherConstant.VOUCHERTYPE, FinancialConstants.STANDARD_VOUCHER_TYPE_PAYMENT);

		headerDetails.put(VoucherConstant.VOUCHERDATE, new Date(payment.getGatewayTransactionDate()));

		headerDetails.put(VoucherConstant.DESCRIPTION,
				"Refund payment for application " + refundApplication.getRefundApplicationNumber());

		headerDetails.put(VoucherConstant.FUNDCODE, payment.getFundCode());

		headerDetails.put(VoucherConstant.DEPARTMENTCODE, payment.getDepartmentCode());

		if (StringUtils.isNotBlank(payment.getFunctionCode())) {

			headerDetails.put(VoucherConstant.FUNCTIONCODE, payment.getFunctionCode());
		}

		final List<HashMap<String, Object>> accountDetails = new ArrayList<>();

		final HashMap<String, Object> payableEntry = new HashMap<>();

		payableEntry.put(VoucherConstant.GLCODE, payment.getPayableGlCode());

		payableEntry.put(VoucherConstant.DEBITAMOUNT, payment.getRefundAmount());

		payableEntry.put(VoucherConstant.CREDITAMOUNT, BigDecimal.ZERO);

		payableEntry.put(VoucherConstant.NARRATION,
				"Refund payable settled for " + refundApplication.getRefundApplicationNumber());

		accountDetails.add(payableEntry);

		final HashMap<String, Object> bankEntry = new HashMap<>();

		bankEntry.put(VoucherConstant.GLCODE, bankAccount.getChartofaccounts().getGlcode());

		bankEntry.put(VoucherConstant.DEBITAMOUNT, BigDecimal.ZERO);

		bankEntry.put(VoucherConstant.CREDITAMOUNT, payment.getRefundAmount());

		bankEntry.put(VoucherConstant.NARRATION, bankAccount.getChartofaccounts().getName());

		accountDetails.add(bankEntry);

		final HashMap<String, String[]> parameters = new HashMap<>();

		parameters.put("paymentMode", new String[] { payment.getRefundMode() });

		parameters.put("grandTotal", new String[] { payment.getRefundAmount().toPlainString() });

		final Paymentheader paymentHeader = paymentService.createPayment(parameters, headerDetails, accountDetails,
				new ArrayList<HashMap<String, Object>>(), bankAccount);

		if (paymentHeader == null || paymentHeader.getVoucherheader() == null) {

			throw new IllegalArgumentException("Finance did not create the refund " + "Payment Voucher");
		}

		/*
		 * Directly approve the refund Payment Voucher. Do not create a workflow state
		 * or employee assignment.
		 */
		paymentHeader.getVoucherheader().setStatus(FinancialConstants.CREATEDVOUCHERSTATUS);

		paymentService.update(paymentHeader);
		entityManager.flush();

		return paymentHeader;
	}
}