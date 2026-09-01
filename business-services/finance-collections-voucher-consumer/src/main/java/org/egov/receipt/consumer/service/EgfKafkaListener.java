/*
 * eGov suite of products aim to improve the internal efficiency,transparency,
 *    accountability and the service delivery of the government  organizations.
 *
 *     Copyright (C) <2015>  eGovernments Foundation
 *
 *     The updated version of eGov suite of products as by eGovernments Foundation
 *     is available at http://www.egovernments.org
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program. If not, see http://www.gnu.org/licenses/ or
 *     http://www.gnu.org/licenses/gpl.html .
 *
 *     In addition to the terms of the GPL license to be adhered to in using this
 *     program, the following additional terms are to be complied with:
 *
 *         1) All versions of this program, verbatim or modified must carry this
 *            Legal Notice.
 *
 *         2) Any misrepresentation of the origin of the material is prohibited. It
 *            is required that all modified versions of this material be marked in
 *            reasonable ways as different from the original version.
 *
 *         3) This license does not grant any rights to any Long of the program
 *            with regards to rights under trademark law for use of the trade names
 *            or trademarks of eGovernments Foundation.
 *
 *   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 */
package org.egov.receipt.consumer.service;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.egov.receipt.consumer.entity.VoucherIntegrationLog;
import org.egov.receipt.consumer.model.Bill;
import org.egov.receipt.consumer.model.BillDetail;
import org.egov.receipt.consumer.model.EgwStatus;
import org.egov.receipt.consumer.model.FinanceMdmsModel;
import org.egov.receipt.consumer.model.ProcessStatus;
import org.egov.receipt.consumer.model.Receipt;
import org.egov.receipt.consumer.model.ReceiptReq;
import org.egov.receipt.consumer.model.Voucher;
import org.egov.receipt.consumer.model.VoucherResponse;
import org.egov.receipt.consumer.repository.VoucherIntegartionLogRepository;
import org.egov.receipt.consumer.util.PaymentUtils;
import org.egov.receipt.consumer.v2.model.PaymentDetail;
import org.egov.receipt.consumer.v2.model.PaymentRequest;
import org.egov.receipt.consumer.v2.model.PaymentStatusEnum;
import org.egov.receipt.custom.exception.VoucherCustomException;
import org.egov.reciept.consumer.config.PropertiesManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import org.egov.receipt.consumer.model.RefundDetail;
import org.egov.receipt.consumer.model.RefundFinanceRequest;
import org.egov.receipt.consumer.model.RefundKafkaDetail;
import org.egov.receipt.consumer.model.RefundKafkaRequest;

@Service
public class EgfKafkaListener {
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private VoucherService voucherService;
	@Autowired
	private ReceiptService receiptService;
	@Autowired
	private InstrumentService instrumentService;
	@Autowired
	private VoucherIntegartionLogRepository voucherIntegartionLogRepository;
	@Autowired
	private PropertiesManager manager;
	@Autowired
	private PaymentUtils payUtils;
	@Autowired
	private RefundFinanceService refundFinanceService;

	private static final String PENDING_WITH_FINANCE = "PENDING_WITH_FINANCE";

	@Value("${refund.finance.debit.glcode}")
	private String refundDebitGlCode;

	@Value("${refund.finance.credit.glcode}")
	private String refundCreditGlCode;

	@Value("${refund.finance.fund.code}")
	private String refundFundCode;

	@Value("${refund.finance.department.code}")
	private String refundDepartmentCode;

	@Value("${refund.finance.function.code:}")
	private String refundFunctionCode;

	public static final Logger LOGGER = LoggerFactory.getLogger(EgfKafkaListener.class);
	private static final String RECEIPT_TYPE = "Receipt";
	private static final String REFUND_TYPE = "Refund";
	@Value("${}")
	private static final String COLLECTION_VERSION = "V2";

	@KafkaListener(topics = { "${egov.collection.receipt.voucher.save.topic}",
			"${egov.collection.receipt.voucher.cancel.topic}", "${kafka.topics.payment.create.name}",
			"${kafka.topics.payment.cancel.name}" })
	public void process(ConsumerRecord<String, String> record) {
		VoucherResponse voucherResponse = null;
		String voucherNumber = "";
		ReceiptReq recRequest = ReceiptReq.builder().build();
		PaymentRequest payRequest = null;
		FinanceMdmsModel finSerMdms = new FinanceMdmsModel();
		try {
			String topic = record.topic();
			if (topic.equals(manager.getCreatePaymentTopicName())
					|| topic.equals(manager.getCancelPaymentTopicName())) {
				payRequest = objectMapper.readValue(record.value(), PaymentRequest.class);
				List<Receipt> receipts = new ArrayList<Receipt>();
				// Wrapper to convert payment request to receipt request
				payUtils.getReceiptsFromPayments(Arrays.asList(payRequest.getPayment()), receipts);
				recRequest.setRequestInfo(payRequest.getRequestInfo());
				recRequest.setReceipt(receipts);
			} else {
				recRequest = objectMapper.readValue(record.value(), ReceiptReq.class);
			}
			LOGGER.info("topic : {} ,  request : {}", topic, recRequest);
			if (voucherService.isTenantEnabledInFinanceModule(recRequest, finSerMdms)) {
				Receipt receipt = recRequest.getReceipt().get(0);
				BillDetail billDetail = receipt.getBill().get(0).getBillDetails().get(0);
				String description = "";
				ProcessStatus status = ProcessStatus.SUCCESS;
				// Fetching the voucher details for the combination of service code and
				// reference document in ERP
				if (topic.equals(manager.getVoucherCreateTopic())) {
					VoucherResponse voucherByServiceAndRefDoc = voucherService.getVoucherByServiceAndRefDoc(
							recRequest.getRequestInfo(), receipt.getTenantId(), billDetail.getBusinessService(),
							billDetail.getReceiptNumber());
					if (voucherService.isVoucherCreationEnabled(recRequest.getReceipt().get(0),
							recRequest.getRequestInfo(), finSerMdms)) {
						/*
						 * Checking existed voucher status if any present if voucher is present with
						 * status != 4 then terminate the process and printing specific log.
						 */
						if (!voucherByServiceAndRefDoc.getVouchers().isEmpty()
								&& !voucherByServiceAndRefDoc.getVouchers().get(0).getStatus().getCode().equals("4")) {
							voucherNumber = voucherByServiceAndRefDoc.getVouchers().get(0).getVoucherNumber();
							throw new VoucherCustomException(ProcessStatus.NA,
									"Already voucher exists (" + voucherNumber + ") for service "
											+ billDetail.getBusinessService() + " with reference number "
											+ billDetail.getReceiptNumber() + ".");
						}
						voucherResponse = voucherService.createReceiptVoucher(recRequest, finSerMdms, null);
						voucherNumber = voucherResponse.getVouchers().get(0).getVoucherNumber();
						receiptService.updateReceipt(recRequest, voucherResponse);
						instrumentService.createInstrument(recRequest, voucherResponse, finSerMdms, null);
						description = "Voucher created successfully with VoucherNumber : " + voucherNumber;
						status = ProcessStatus.SUCCESS;
					} else {
						// Todo : Status should be different
						description = "Voucher creation is not enabled for business service code : " + recRequest
								.getReceipt().get(0).getBill().get(0).getBillDetails().get(0).getBusinessService();
						status = ProcessStatus.SUCCESS;
					}
					this.getBackupToDB(recRequest, status, description, voucherNumber);
				} else if (topic.equals(manager.getVoucherCancelTopic())) {
					/*
					 * Checking voucher existence in ERP for the combination. if not found then
					 * throwing exception with Not Found if voucher found cross checking the request
					 * voucher number and erp existed voucher number.if both are same then based on
					 * status it will proceed ahead. if status!=4 then proceed ahead to cancel the
					 * erp existed voucher. if status=4 then terminating execution and feeding data
					 * to table with specific message.
					 */
					VoucherResponse voucherByServiceAndRefDoc = voucherService.getVoucherByServiceAndRefDoc(
							recRequest.getRequestInfo(), receipt.getTenantId(), billDetail.getBusinessService(),
							billDetail.getReceiptNumber());
					if (!voucherByServiceAndRefDoc.getVouchers().isEmpty()) {
						Voucher voucher = voucherByServiceAndRefDoc.getVouchers().get(0);
						String erpVoucherNumber = voucher.getVoucherNumber();
						String reqVoucherNumber = recRequest.getReceipt().get(0).getBill().get(0).getBillDetails()
								.get(0).getVoucherHeader();
						if (voucher.getStatus().getCode().equals("4")) {
							status = ProcessStatus.NA;
							description = "Voucher(" + erpVoucherNumber + ") associated with service "
									+ billDetail.getBusinessService() + " and reference number "
									+ billDetail.getReceiptNumber() + " is already in Cancelled status.";
							if (!reqVoucherNumber.isEmpty() && !erpVoucherNumber.equals(reqVoucherNumber)) {
								description += "However, we found that the mapping voucher reference sent("
										+ reqVoucherNumber + ") is incorrect.";
							}
						} else {
							description = "Voucher number : " + erpVoucherNumber + " is CANCELLED successfully!";
							if (!reqVoucherNumber.isEmpty() && !erpVoucherNumber.equals(reqVoucherNumber)) {
								description = "Voucher(" + erpVoucherNumber + ") associated with service "
										+ billDetail.getBusinessService() + " and reference number "
										+ billDetail.getReceiptNumber() + " is Cancelled. "
										+ "However, we found that the mapping voucher reference sent("
										+ reqVoucherNumber + ") is incorrect.";
							}
							recRequest.getReceipt().get(0).getBill().get(0).getBillDetails().get(0)
									.setVoucherHeader(erpVoucherNumber);
							voucherService.cancelReceiptVoucher(recRequest, this.getTenantId(recRequest),
									Collections.singleton(erpVoucherNumber));
							instrumentService.cancelInstrument(recRequest, finSerMdms);
						}
						this.getBackupToDB(recRequest, status, description, erpVoucherNumber);
					} else {
						throw new VoucherCustomException(ProcessStatus.FAILED,
								"Voucher is not exist for service " + billDetail.getBusinessService()
										+ " with reference number " + billDetail.getReceiptNumber());
					}
				} else if (topic.equals(manager.getCreatePaymentTopicName())) {
					// create voucher for collection v2
					ReceiptReq recRequestTemp = ReceiptReq.builder().requestInfo(recRequest.getRequestInfo()).build();
					for (Receipt recpt : recRequest.getReceipt()) {
						recRequestTemp.setReceipt(Arrays.asList(recpt));
						Bill bill = recpt.getBill().get(0);
						VoucherResponse voucherByServiceAndRefDoc = voucherService.getVoucherByServiceAndRefDoc(
								recRequestTemp.getRequestInfo(), recpt.getTenantId(), null, recpt.getPaymentId());
						if (voucherService.isVoucherCreationEnabled(recpt, recRequestTemp.getRequestInfo(),
								finSerMdms)) {
							if (!voucherByServiceAndRefDoc.getVouchers().isEmpty() && !voucherByServiceAndRefDoc
									.getVouchers().get(0).getStatus().getCode().equals("4")) {
								voucherNumber = voucherByServiceAndRefDoc.getVouchers().get(0).getVoucherNumber();
								throw new VoucherCustomException(ProcessStatus.NA, String.format(
										"Already voucher exists (%1$s) for service %2$s with reference number: %3$s.",
										voucherNumber, bill.getBusinessService(), recpt.getPaymentId()));
							}
							VoucherResponse createReceiptVoucher = voucherService.createReceiptVoucher(recRequestTemp,
									finSerMdms, COLLECTION_VERSION);
							if (voucherResponse == null) {
								voucherResponse = createReceiptVoucher;
							} else {
								voucherResponse.getVouchers().addAll(createReceiptVoucher.getVouchers());
							}
							if (voucherNumber.isEmpty()) {
								voucherNumber = voucherResponse.getVouchers().get(0).getVoucherNumber();
							} else {
								voucherNumber = ", " + voucherNumber;
							}
//	        				receiptService.updateReceipt(recRequest, voucherResponse);
						}
					}
					instrumentService.createInstrument(recRequest, voucherResponse, finSerMdms, COLLECTION_VERSION);
					description = String.format("Voucher created successfully with VoucherNumber : %1$s",
							voucherNumber);
					status = ProcessStatus.SUCCESS;
					this.getBackupToDB(payRequest, status, description, voucherNumber);
				} else if (topic.equals(manager.getCancelPaymentTopicName())
						&& payRequest.getPayment().getPaymentStatus().equals(PaymentStatusEnum.CANCELLED)) {
					String paymentId = payRequest.getPayment().getId();
					VoucherResponse voucherByServiceAndRefDoc = voucherService.getVoucherByServiceAndRefDoc(
							recRequest.getRequestInfo(), receipt.getTenantId(), null, paymentId);
					if (!voucherByServiceAndRefDoc.getVouchers().isEmpty()) {
						Set<String> voucherNumbers = voucherByServiceAndRefDoc.getVouchers().stream()
								.map(Voucher::getVoucherNumber).collect(Collectors.toSet());
						if (voucherByServiceAndRefDoc.getVouchers().stream().map(Voucher::getStatus)
								.anyMatch(stat -> Integer.parseInt(stat.getCode()) != 4)) {
							voucherService.cancelReceiptVoucher(recRequest, this.getTenantId(recRequest),
									voucherNumbers);
							instrumentService.cancelInstrumentForPayment(payRequest, finSerMdms);
							description = String.format(
									"Voucher number : %1$s is CANCELLED successfully for Payment id: %2$s",
									voucherNumbers, paymentId);
							this.getBackupToDB(payRequest, ProcessStatus.SUCCESS, description,
									voucherNumbers.toString());
						} else {
							String format = String.format(
									"One/All of the Vouchers(%1$s) associated with Payment id %2$s is/are already in Cancelled state.",
									voucherNumbers, paymentId);
							throw new VoucherCustomException(ProcessStatus.FAILED, format);
						}
					} else {
						throw new VoucherCustomException(ProcessStatus.FAILED,
								String.format("Vouchers is not exist for Payment id: %1$s", paymentId));
					}
				} else if (topic.equals(manager.getCancelPaymentTopicName())
						&& payRequest.getPayment().getPaymentStatus().equals(PaymentStatusEnum.DISHONOURED)) {
					LOGGER.info("Payment dishonoured successfully.");
				}
			}
		} catch (VoucherCustomException e) {
			this.getBackupToDB(recRequest, e.getStatus(), e.getMessage(), voucherNumber);
			LOGGER.error(e.getMessage());
		} catch (Exception e) {
			this.getBackupToDB(recRequest, ProcessStatus.FAILED, e.getMessage(), voucherNumber);
			LOGGER.error(e.getMessage());
		}
	}

	@KafkaListener(topics = "${egov.refund.finance.process.topic}")
	public void processRefundFinance(final ConsumerRecord<String, String> record) {

		RefundFinanceRequest financeRequest = null;

		try {
			final RefundKafkaRequest kafkaRequest = objectMapper.readValue(record.value(), RefundKafkaRequest.class);

			validateRefundKafkaRequest(kafkaRequest);

			final RefundKafkaDetail sourceRefund = kafkaRequest.getRefund();

			final String workflowState = sourceRefund.getProcessInstance().getState().getState();

			if (!PENDING_WITH_FINANCE.equalsIgnoreCase(workflowState)) {

				LOGGER.info("Skipping refund {} because workflow state is {}", sourceRefund.getRefundNo(),
						workflowState);

				return;
			}

			financeRequest = mapToFinanceRequest(kafkaRequest);

			validateRefundFinanceRequest(financeRequest);

			final RefundDetail financeRefund = financeRequest.getRefund();

			LOGGER.info("Submitting refund {} for tenant {} " + "to Finance approval",
					financeRefund.getRefundApplicationNumber(), financeRefund.getTenantId());

			refundFinanceService.createRefundApplication(financeRequest);

			saveRefundIntegrationLog(financeRequest, ProcessStatus.SUCCESS,
					"Refund application submitted successfully " + "to Finance for approval", "");

			LOGGER.info("Finance refund application created successfully: {}",
					financeRefund.getRefundApplicationNumber());

		} catch (VoucherCustomException exception) {

			saveRefundIntegrationLog(financeRequest, exception.getStatus(), exception.getMessage(), "");

			LOGGER.error("Unable to create Finance refund application: {}", exception.getMessage(), exception);

		} catch (Exception exception) {

			saveRefundIntegrationLog(financeRequest, ProcessStatus.FAILED, exception.getMessage(), "");

			LOGGER.error("Unexpected error while processing Finance " + "refund application", exception);
		}
	}

	private void validateRefundKafkaRequest(final RefundKafkaRequest kafkaRequest) throws VoucherCustomException {

		if (kafkaRequest == null) {
			throw new VoucherCustomException(ProcessStatus.FAILED, "Refund Kafka request is missing");
		}

		if (kafkaRequest.getRequestInfo() == null) {
			throw new VoucherCustomException(ProcessStatus.FAILED, "RequestInfo is missing in refund Kafka request");
		}

		final RefundKafkaDetail refund = kafkaRequest.getRefund();

		if (refund == null) {
			throw new VoucherCustomException(ProcessStatus.FAILED, "Refund details are missing");
		}

		if (StringUtils.isEmpty(refund.getTenantId())) {
			throw new VoucherCustomException(ProcessStatus.FAILED, "Tenant ID is missing");
		}

		if (StringUtils.isEmpty(refund.getRefundNo())) {
			throw new VoucherCustomException(ProcessStatus.FAILED, "Refund number is missing");
		}

		if (StringUtils.isEmpty(refund.getModuleName())) {
			throw new VoucherCustomException(ProcessStatus.FAILED, "Module name is missing");
		}

		if (StringUtils.isEmpty(refund.getBusinessService())) {
			throw new VoucherCustomException(ProcessStatus.FAILED, "Business service is missing");
		}

		if (StringUtils.isEmpty(refund.getConsumerCode())) {
			throw new VoucherCustomException(ProcessStatus.FAILED, "Consumer code is missing");
		}

		if (StringUtils.isEmpty(refund.getPaymentId())) {
			throw new VoucherCustomException(ProcessStatus.FAILED, "Payment ID is missing");
		}

		if (refund.getRefundAmount() == null || refund.getRefundAmount().compareTo(BigDecimal.ZERO) <= 0) {
			throw new VoucherCustomException(ProcessStatus.FAILED, "Refund amount must be greater than zero");
		}

		if (refund.getProcessInstance() == null || refund.getProcessInstance().getState() == null
				|| StringUtils.isEmpty(refund.getProcessInstance().getState().getState())) {
			throw new VoucherCustomException(ProcessStatus.FAILED, "Refund workflow state is missing");
		}
	}

	private RefundFinanceRequest mapToFinanceRequest(final RefundKafkaRequest kafkaRequest) {

		final RefundKafkaDetail sourceRefund = kafkaRequest.getRefund();

		final String moduleName = sourceRefund.getModuleName() == null ? null
				: sourceRefund.getModuleName().trim().toUpperCase();

		final RefundDetail financeRefund = RefundDetail.builder()
				/*
				 * Do not map sourceRefund.id. Finance has its own application ID.
				 */
				.tenantId(sourceRefund.getTenantId()).moduleName(moduleName)
				.businessService(sourceRefund.getBusinessService()).refundApplicationNumber(sourceRefund.getRefundNo())
				.referenceNumber(sourceRefund.getConsumerCode()).paymentId(sourceRefund.getPaymentId())
				.receiptNumber(null).refundAmount(sourceRefund.getRefundAmount())
				.refundReason(sourceRefund.getRefundReason())
				/*
				 * Refund Service does not send a refund date. Finance will use the current date
				 * while creating the JV.
				 */
				.refundDate(null)
				/*
				 * Do not forward INITIATE. Finance owns its own processing status.
				 */
				.status(null).debitGlCode(refundDebitGlCode).creditGlCode(refundCreditGlCode).fundCode(refundFundCode)
				.departmentCode(refundDepartmentCode)
				.functionCode(StringUtils.isEmpty(refundFunctionCode) ? null : refundFunctionCode).build();

		return RefundFinanceRequest.builder().requestInfo(kafkaRequest.getRequestInfo())
				.tenantId(sourceRefund.getTenantId()).refund(financeRefund).build();
	}

	/**
	 * Validates the mandatory refund details required for creating a refund
	 * approval application in Finance.
	 */
	private void validateRefundFinanceRequest(RefundFinanceRequest refundRequest) throws VoucherCustomException {

		if (refundRequest == null) {
			throw new VoucherCustomException(ProcessStatus.FAILED, "Refund Finance request is missing");
		}

		if (refundRequest.getRequestInfo() == null) {
			throw new VoucherCustomException(ProcessStatus.FAILED, "RequestInfo is missing in refund Finance request");
		}

		if (refundRequest.getRefund() == null) {
			throw new VoucherCustomException(ProcessStatus.FAILED, "Refund details are missing");
		}

		RefundDetail refund = refundRequest.getRefund();

		if (StringUtils.isEmpty(refundRequest.getTenantId())) {
			refundRequest.setTenantId(refund.getTenantId());
		}

		if (StringUtils.isEmpty(refund.getTenantId())) {
			refund.setTenantId(refundRequest.getTenantId());
		}

		if (StringUtils.isEmpty(refundRequest.getTenantId()) || StringUtils.isEmpty(refund.getTenantId())) {
			throw new VoucherCustomException(ProcessStatus.FAILED, "Tenant ID is missing in refund Finance request");
		}

		if (!refundRequest.getTenantId().equals(refund.getTenantId())) {
			throw new VoucherCustomException(ProcessStatus.FAILED, "Root tenant ID and refund tenant ID do not match");
		}

		if (StringUtils.isEmpty(refund.getDebitGlCode())) {
			throw new VoucherCustomException(ProcessStatus.FAILED, "Debit GL code is missing");
		}

		if (StringUtils.isEmpty(refund.getCreditGlCode())) {
			throw new VoucherCustomException(ProcessStatus.FAILED, "Credit GL code is missing");
		}

		if (StringUtils.isEmpty(refund.getFundCode())) {
			throw new VoucherCustomException(ProcessStatus.FAILED, "Fund code is missing");
		}

		if (StringUtils.isEmpty(refund.getDepartmentCode())) {
			throw new VoucherCustomException(ProcessStatus.FAILED, "Department code is missing");
		}

		if (StringUtils.isEmpty(refund.getBusinessService())) {
			throw new VoucherCustomException(ProcessStatus.FAILED, "Business service is missing in refund details");
		}

		if (StringUtils.isEmpty(refund.getRefundApplicationNumber())) {

			throw new VoucherCustomException(ProcessStatus.FAILED, "Refund application number is missing");
		}

		if (refund.getRefundAmount() == null || refund.getRefundAmount().compareTo(BigDecimal.ZERO) <= 0) {

			throw new VoucherCustomException(ProcessStatus.FAILED, "Refund amount must be greater than zero");
		}

	}

	/**
	 * Saves the success or failure details of refund application integration into
	 * the voucher integration log.
	 */
	private void saveRefundIntegrationLog(RefundFinanceRequest refundRequest, ProcessStatus status, String description,
			String voucherNumber) {

		if (refundRequest == null || refundRequest.getRefund() == null) {
			LOGGER.error("Unable to save refund integration log " + "because refund request is unavailable");
			return;
		}

		try {
			RefundDetail refund = refundRequest.getRefund();

			VoucherIntegrationLog voucherIntegrationLog = new VoucherIntegrationLog();

			voucherIntegrationLog.setStatus(status.name());

			voucherIntegrationLog.setDescription(description);

			voucherIntegrationLog.setReferenceNumber(refund.getRefundApplicationNumber());

			voucherIntegrationLog.setTenantId(refund.getTenantId());

			voucherIntegrationLog.setVoucherNumber(voucherNumber);

			voucherIntegrationLog.setType(REFUND_TYPE);

			voucherIntegrationLog.setRequestJson(objectMapper.writeValueAsString(refundRequest));

			voucherIntegrationLog.setCreatedDate(new Date());

			voucherIntegartionLogRepository.saveVoucherIntegrationLog(voucherIntegrationLog);

			LOGGER.debug("Refund voucher integration status: {}, message: {}", status, description);

		} catch (Exception e) {
			LOGGER.error("Error occurred while saving refund " + "voucher integration log: {}", e.getMessage());
		}
	}

	private String getTenantId(ReceiptReq recRequest) {
		return recRequest.getReceipt().get(0).getTenantId();
	}

	/**
	 * function use to take a backup to DB after success/failure of voucher creation
	 * process.
	 */
	private void getBackupToDB(Object request, ProcessStatus status, String description, String voucherNumber) {
		try {
			VoucherIntegrationLog voucherIntegrationLog = new VoucherIntegrationLog();
			voucherIntegrationLog.setStatus(status.name());
			voucherIntegrationLog.setDescription(description);
			this.prepareVoucherIntegrationLog(voucherIntegrationLog, request, voucherNumber);
			voucherIntegartionLogRepository.saveVoucherIntegrationLog(voucherIntegrationLog);
			LOGGER.debug(String.format("[Status : %1$s, message : %2$s]", status, description));
		} catch (Exception e) {
			LOGGER.error("ERROR occurred while doing a backup to databases. " + e.getMessage());
		}
	}

	private void prepareVoucherIntegrationLog(VoucherIntegrationLog voucherIntegrationLog, Object request,
			String voucherNumber) {
		if (request instanceof PaymentRequest) {
			PaymentRequest payReq = (PaymentRequest) request;
			Set<String> voucherNumbers = payReq.getPayment().getPaymentDetails().stream()
					.map(PaymentDetail::getReceiptNumber).collect(Collectors.toSet());
			voucherIntegrationLog.setReferenceNumber(voucherNumbers.toString());
			voucherIntegrationLog.setTenantId(payReq.getPayment().getTenantId());
		} else {
			ReceiptReq request1 = (ReceiptReq) request;
			voucherIntegrationLog.setReferenceNumber(
					request1.getReceipt().get(0).getBill().get(0).getBillDetails().get(0).getReceiptNumber());
			voucherIntegrationLog.setTenantId(request1.getReceipt().get(0).getTenantId());
		}
		ObjectMapper mappper = new ObjectMapper();
		try {
			String jsonReq = mappper.writeValueAsString(request);
			voucherIntegrationLog.setRequestJson(jsonReq);
		} catch (JsonProcessingException e) {
			LOGGER.error("ERROR occurred while parsing the ReceiptRequest " + e.getMessage());
		}
		voucherIntegrationLog.setVoucherNumber(voucherNumber);
		voucherIntegrationLog.setType(RECEIPT_TYPE);
		voucherIntegrationLog.setCreatedDate(new Date());
	}
}
