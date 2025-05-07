package org.egov.pg.service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.egov.pg.config.AppProperties;
import org.egov.pg.models.BankAccount;
import org.egov.pg.models.BankAccountResponse;
import org.egov.pg.models.BankAccountSearchCriteria;
import org.egov.pg.models.Notes;
import org.egov.pg.models.Transaction;
import org.egov.pg.models.Transaction.TxnStatusEnum;
import org.egov.pg.models.TransactionRequest;
import org.egov.pg.models.Transfer;
import org.egov.pg.models.TransferWrapper;
import org.egov.pg.models.enums.TxnSettlementStatus;
import org.egov.pg.producer.Producer;
import org.egov.pg.web.models.RequestInfoWrapper;
import org.egov.pg.web.models.TransactionCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TransactionsSchedulerService {

	@Autowired
	private TransactionService transactionService;

	@Autowired
	private BankAccountService bankAccountService;

	@Autowired
	private Producer producer;

	@Autowired
	private AppProperties appProperties;

	@Autowired
	private GatewayService gatewayService;

	public Object transferAmount(RequestInfoWrapper requestInfoWrapper) {

		Set<String> tenantIds = null;
		BankAccountResponse bankAccountResponse = null;
		List<Transfer> transferList = new ArrayList<>();

		TransactionCriteria transactionCriteria = TransactionCriteria.builder().txnStatus(TxnStatusEnum.SUCCESS)
				.startDateTime(
						LocalDate.now().minusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli())
				.endDateTime(LocalDate.now().minusDays(1).atTime(23, 59, 59, 999_999_999).atZone(ZoneId.systemDefault())
						.toInstant().toEpochMilli())
				.build();

		List<Transaction> transactions = transactionService.getTransactions(transactionCriteria);

		if (!CollectionUtils.isEmpty(transactions)) {
			tenantIds = transactions.stream().map(Transaction::getTenantId).collect(Collectors.toSet());
		}

		if (!CollectionUtils.isEmpty(tenantIds)) {
			BankAccountSearchCriteria bankAccountSearchCriteria = BankAccountSearchCriteria.builder()
					.requestInfo(requestInfoWrapper.getRequestInfo()).tenantIds(tenantIds).active(true).build();
			// fetch all bank account
			bankAccountResponse = bankAccountService.searchBankAccount(bankAccountSearchCriteria);
		}

		if (null != bankAccountResponse && !CollectionUtils.isEmpty(bankAccountResponse.getBankAccounts())) {
			// filter if payTo is null or empty
			bankAccountResponse.setBankAccounts(bankAccountResponse.getBankAccounts().stream()
					.filter(bankAccount -> !StringUtils.isEmpty(bankAccount.getPayTo())).collect(Collectors.toList()));

			Map<String, List<BankAccount>> bankAccountTenantIdMap = bankAccountResponse.getBankAccounts().stream()
					.collect(Collectors.groupingBy(BankAccount::getTenantId));

			transactions.stream()
					.forEach(transaction -> bankAccountTenantIdMap.entrySet().stream()
							.filter(bankAccountTenantIds -> transaction.getTenantId() != null
									&& !bankAccountTenantIds.getValue().isEmpty()
									&& transaction.getTenantId()
											.equalsIgnoreCase(bankAccountTenantIds.getValue().get(0).getTenantId()))
							.findFirst().ifPresent(bankAccountTenantIds -> {
								BankAccount bankAccount = bankAccountTenantIds.getValue().get(0);
								String ulbName = transaction.getTenantId().split("\\.").length > 1
										? transaction.getTenantId().split("\\.")[1]
										: "";
								// enrich transfer object
								Transfer transfer = Transfer.builder().account(bankAccount.getPayTo())
										.amount(null != transaction.getTxnAmount()
												? Integer.parseInt(transaction.getTxnAmount().replace(".", ""))
												: 0)
										.notes(Notes.builder().ulbName(ulbName).orderId(transaction.getOrderId())
												.gatewayTxnId(transaction.getGatewayTxnId()).build())
										.build();

								TransferWrapper transferWrapper = TransferWrapper.builder()
										.transfers(Collections.singletonList(transfer)).build();

								// call settlement api
								Object settlementAmountResponse = gatewayService.settlementAmount(transaction,
										transferWrapper);

								transaction.setTxnSettlementStatus(TxnSettlementStatus.CREATED.name());
								transaction.setSettlementResponse(settlementAmountResponse);

								TransactionRequest transactionRequest = TransactionRequest.builder()
										.requestInfo(requestInfoWrapper.getRequestInfo()).transaction(transaction)
										.build();

								// update transaction
								producer.push(appProperties.getUpdateTxnTopic(), transactionRequest);

								transferList.add(transfer);
							}));
		}

		return transferList;
	}

//	Map<String, List<BankAccount>> bankAccountModuleMap = bankAccountResponse.getBankAccounts().stream()
//	.collect(Collectors.groupingBy(BankAccount::getDescription));
//
//Map<String, List<Transaction>> transactionsModuleMap = transactions.stream()
//	.collect(Collectors.groupingBy(Transaction::getModule));
//
//transactionsModuleMap.forEach((moduleKey, transactionsModule) -> {
//Map<String, List<Transaction>> transactionsTenantIdMap = transactionsModule.stream()
//		.collect(Collectors.groupingBy(Transaction::getTenantId));
//
//transactionsTenantIdMap.forEach((tenantId, tenantTransactions) -> {
//
//	tenantTransactions.stream().forEach(transaction -> {
//		bankAccountModuleMap.getOrDefault(moduleKey, new ArrayList<>()).stream()
//				.filter(bankAccountModule -> tenantId.equalsIgnoreCase(bankAccountModule.getTenantId()))
//				.findFirst().ifPresent(bankAccountModule -> {
//					String ulbName = null != transaction.getTenantId()
//							? transaction.getTenantId().split("\\.")[1]
//							: "";
//					// enrich transfer object
//					Transfer transfer = Transfer.builder().account(bankAccountModule.getPayTo())
//							.amount(null != transaction.getTxnAmount()
//									? Integer.parseInt(transaction.getTxnAmount().replace(".", ""))
//									: 0)
//							.notes(Notes.builder().ulbName(ulbName).orderId(transaction.getOrderId())
//									.gatewayTxnId(transaction.getGatewayTxnId()).build())
//							.build();
//
////					System.err.println(transfer);
//
//					TransferWrapper transferWrapper = TransferWrapper.builder()
//							.transfers(Collections.singletonList(transfer)).build();
//
//					System.err.println(transferWrapper);
//
//					// call settlement api // TODO
//
//					transaction.setTxnSettlementStatus("CREATED");
//					transaction.setSettlementResponse(null);
//					System.err.println(transaction);
//
//					TransactionRequest transactionRequest = TransactionRequest.builder()
//							.requestInfo(requestInfoWrapper.getRequestInfo()).transaction(transaction)
//							.build();
//
////					producer.push(appProperties.getUpdateTxnTopic(), transactionRequest);
//
//					transferList.add(transfer);
//
//				});
//
//	});
//
//});
//});

}
