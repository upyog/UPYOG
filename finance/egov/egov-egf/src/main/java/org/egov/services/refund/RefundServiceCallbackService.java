package org.egov.services.refund;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.lang.StringUtils;
import org.egov.egf.contract.model.RefundStatusProcessInstance;
import org.egov.egf.contract.model.RefundStatusUpdate;
import org.egov.egf.contract.model.RefundStatusUpdateRequest;
import org.egov.infra.microservice.models.RequestInfo;
import org.egov.infra.microservice.utils.MicroserviceUtils;
import org.egov.model.refund.RefundApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

public class RefundServiceCallbackService {

	private static final Logger LOG = LoggerFactory.getLogger(RefundServiceCallbackService.class);

	public static final String FINANCE_APPROVED = "FINANCE_APPROVED";

	public static final String FINANCE_REJECTED = "FINANCE_REJECTED";

	private static final String ACTION_APPROVE = "APPROVE";

	private static final String ACTION_REJECT = "REJECT";

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	private MicroserviceUtils microserviceUtils;

	@Value("${egov.services.refund.host:}")
	private String refundServiceHost;

	@Value("${egov.services.refund.update.url:}")
	private String refundUpdateUrl;

	/**
	 * Registers the refund-service callback after the current Finance transaction
	 * successfully commits.
	 *
	 * If the voucher is not connected to a refund application, nothing is
	 * performed. Therefore, existing normal JV flows are unaffected.
	 */
	public void notifyRefundStatusAfterCommit(final String voucherNumber, final String financeStatus,
			final String approvalComments) {

		if (StringUtils.isBlank(voucherNumber)) {
			LOG.warn("Refund callback skipped because voucher number is empty");
			return;
		}

		if (!FINANCE_APPROVED.equals(financeStatus) && !FINANCE_REJECTED.equals(financeStatus)) {
			LOG.warn("Refund callback skipped for voucher {} because " + "status {} is unsupported", voucherNumber,
					financeStatus);
			return;
		}

		final RefundApplication refundApplication = findByVoucherNumber(voucherNumber);

		if (refundApplication == null) {
			LOG.debug("Voucher {} is not connected to a refund application; " + "refund callback skipped",
					voucherNumber);
			return;
		}

		validateConfiguration();

		/*
		 * Build the complete payload while the Finance request context and
		 * authentication thread-locals are still available.
		 */
		final RefundStatusUpdateRequest callbackRequest = buildCallbackRequest(refundApplication, financeStatus,
				approvalComments);

		final String callbackUrl = buildCallbackUrl();

		if (!TransactionSynchronizationManager.isSynchronizationActive()) {
			/*
			 * This should not happen when called from the transactional JV update flow.
			 * Avoid sending an update before Finance data has committed.
			 */
			LOG.error("Refund callback was not registered for voucher {} " + "because no active transaction was found",
					voucherNumber);
			return;
		}

		TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {

			@Override
			public void afterCommit() {
				sendCallbackSafely(callbackUrl, callbackRequest, voucherNumber, financeStatus);
			}
		});

		LOG.info("Refund callback registered for voucher {} with status {}", voucherNumber, financeStatus);
	}

	private RefundApplication findByVoucherNumber(final String voucherNumber) {

		final List<RefundApplication> applications = entityManager
				.createQuery("from RefundApplication " + "where voucherNumber = :voucherNumber",
						RefundApplication.class)
				.setParameter("voucherNumber", voucherNumber).setMaxResults(1).getResultList();

		return applications.isEmpty() ? null : applications.get(0);
	}

	private RefundStatusUpdateRequest buildCallbackRequest(final RefundApplication refundApplication,
			final String financeStatus, final String approvalComments) {

		final long eventTime = new Date().getTime();

		final String workflowAction = FINANCE_APPROVED.equals(financeStatus) ? ACTION_APPROVE : ACTION_REJECT;

		final RequestInfo requestInfo = new RequestInfo();
		requestInfo.setApiId("Rainmaker");
		requestInfo.setVer("1.0");
		requestInfo.setAction("UPDATE");
		requestInfo.setTs(eventTime);
		requestInfo.setMsgId(refundApplication.getRefundApplicationNumber() + "-JV-" + financeStatus);

		final String serviceAuthToken = microserviceUtils.generateAdminToken(refundApplication.getTenantId());

		if (StringUtils.isBlank(serviceAuthToken)) {
			throw new IllegalStateException("Unable to generate service auth token " + "for refund-service callback");
		}

		requestInfo.setAuthToken(serviceAuthToken);

		requestInfo.setUserInfo(microserviceUtils.getServiceUserInfo(serviceAuthToken));

		final RefundStatusProcessInstance processInstance = new RefundStatusProcessInstance();

		processInstance.setTenantId(refundApplication.getTenantId());
		processInstance.setBusinessService(refundApplication.getBusinessService());
		processInstance.setBusinessId(refundApplication.getRefundApplicationNumber());
		processInstance.setAction(workflowAction);
		processInstance.setModuleName(refundApplication.getModuleName());
		processInstance.setComment(resolveComment(financeStatus, approvalComments));

		final RefundStatusUpdate refundStatusUpdate = new RefundStatusUpdate();

		refundStatusUpdate.setRefundNo(refundApplication.getRefundApplicationNumber());
		refundStatusUpdate.setTenantId(refundApplication.getTenantId());
		refundStatusUpdate.setModuleName(refundApplication.getModuleName());
		refundStatusUpdate.setBusinessService(refundApplication.getBusinessService());
		refundStatusUpdate.setConsumerCode(refundApplication.getReferenceNumber());
		refundStatusUpdate.setPaymentId(refundApplication.getPaymentId());
		refundStatusUpdate.setRefundAmount(refundApplication.getRefundAmount());
		refundStatusUpdate.setStatus(financeStatus);
		refundStatusUpdate.setSanctionRef(refundApplication.getVoucherNumber());
		refundStatusUpdate.setFinanceApprovalDate(eventTime);
		refundStatusUpdate.setProcessInstance(processInstance);

		final RefundStatusUpdateRequest callbackRequest = new RefundStatusUpdateRequest();

		callbackRequest.setRequestInfo(requestInfo);
		callbackRequest.setRefund(refundStatusUpdate);

		return callbackRequest;
	}

	private String resolveComment(final String financeStatus, final String approvalComments) {

		if (StringUtils.isNotBlank(approvalComments)) {
			return approvalComments.trim();
		}

		if (FINANCE_APPROVED.equals(financeStatus)) {
			return "Refund Journal Voucher approved";
		}

		return "Refund Journal Voucher rejected";
	}

	private String buildCallbackUrl() {

		final boolean hostEndsWithSlash = refundServiceHost.endsWith("/");

		final boolean pathStartsWithSlash = refundUpdateUrl.startsWith("/");

		if (hostEndsWithSlash && pathStartsWithSlash) {
			return refundServiceHost + refundUpdateUrl.substring(1);
		}

		if (!hostEndsWithSlash && !pathStartsWithSlash) {
			return refundServiceHost + "/" + refundUpdateUrl;
		}

		return refundServiceHost + refundUpdateUrl;
	}

	private void validateConfiguration() {

		if (StringUtils.isBlank(refundServiceHost)) {
			throw new IllegalStateException("Refund-service host is not configured");
		}

		if (StringUtils.isBlank(refundUpdateUrl)) {
			throw new IllegalStateException("Refund-service update URL is not configured");
		}
	}

	private void sendCallbackSafely(final String callbackUrl, final RefundStatusUpdateRequest callbackRequest,
			final String voucherNumber, final String financeStatus) {

		try {
			final RestTemplate restTemplate = microserviceUtils.createRestTemplate();

			final Map<?, ?> response = restTemplate.postForObject(callbackUrl, callbackRequest, Map.class);

			LOG.info("Refund-service callback completed for voucher {} " + "with status {}. Response received: {}",
					voucherNumber, financeStatus, response != null);

		} catch (RestClientException exception) {
			LOG.error("Refund-service callback failed for voucher {} " + "with status {}", voucherNumber, financeStatus,
					exception);

		} catch (Exception exception) {
			LOG.error("Unexpected refund callback error for voucher {} " + "with status {}", voucherNumber,
					financeStatus, exception);
		}
	}
}