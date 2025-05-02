package org.egov.pg.service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.egov.pg.models.BankAccount;
import org.egov.pg.models.BankAccountResponse;
import org.egov.pg.models.Notes;
import org.egov.pg.models.Transaction;
import org.egov.pg.models.Transaction.TxnStatusEnum;
import org.egov.pg.models.Transfer;
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

	public Object transferAmount(RequestInfoWrapper requestInfoWrapper) {

		Set<String> tenantIds = null;
		BankAccountResponse bankAccountResponse = null;
		List<Transfer> transferList = new ArrayList<>();

		TransactionCriteria transactionCriteria = TransactionCriteria.builder().txnStatus(TxnStatusEnum.SUCCESS)
				.startDateTime(
						LocalDate.now().minusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli())
				.endDateTime(LocalDate.now().minusDays(1).atTime(23, 59, 59, 999_999_999).atZone(ZoneId.systemDefault())
						.toInstant().toEpochMilli())
				.build(); // TODO

		List<Transaction> transactions = transactionService.getTransactions(transactionCriteria);

		if (!CollectionUtils.isEmpty(transactions)) {
			tenantIds = transactions.stream().map(Transaction::getTenantId).collect(Collectors.toSet());
		}

		if (!CollectionUtils.isEmpty(tenantIds)) {
			// fetch all bank account
			bankAccountResponse = bankAccountService.searchBankAccount(tenantIds, requestInfoWrapper.getRequestInfo());
		}
		if (null != bankAccountResponse && !CollectionUtils.isEmpty(bankAccountResponse.getBankAccounts())) {

			Map<String, List<BankAccount>> bankAccountModuleMap = bankAccountResponse.getBankAccounts().stream()
					.collect(Collectors.groupingBy(BankAccount::getDescription));

			Map<String, List<Transaction>> transactionsModuleMap = transactions.stream()
					.collect(Collectors.groupingBy(Transaction::getModule));

			transactionsModuleMap.forEach((moduleKey, transactionsModule) -> {
				Map<String, List<Transaction>> transactionsTenantIdMap = transactionsModule.stream()
						.collect(Collectors.groupingBy(Transaction::getTenantId));

				transactionsTenantIdMap.forEach((tenantId, tenantTransactions) -> {

					tenantTransactions.stream().forEach(transaction -> {
						bankAccountModuleMap.getOrDefault(moduleKey, new ArrayList<>()).stream()
								.filter(bankAccountModule -> tenantId.equalsIgnoreCase(bankAccountModule.getTenantId()))
								.findFirst().ifPresent(bankAccountModule -> {
									String ulbName = null != transaction.getTenantId()
											? transaction.getTenantId().split("\\.")[1]
											: "";
									// enrich transfer list object
									transferList.add(Transfer.builder().account(bankAccountModule.getPayTo())
											.amount(null != transaction.getTxnAmount()
													? Integer.parseInt(transaction.getTxnAmount().replace(".", ""))
													: 0)
											.notes(Notes.builder().ulbName(ulbName).orderId(transaction.getOrderId())
													.gatewayTxnId(transaction.getGatewayTxnId()).build())
											.build());
								});

					});

				});
			});
		}

		if (!CollectionUtils.isEmpty(transferList)) {

			System.err.println(transferList);

//			Integer totalAmount = transferList.stream()
//					.filter(transfer -> null != transfer && null != transfer.getAmount()).map(Transfer::getAmount)
//					.reduce(0, Integer::sum);
//
			transferList.stream().forEach(transfer -> {
				System.err.println(transfer);

			});
//			Orders orders = Orders.builder().amount(totalAmount).transfers(transferList).build();

			// call settlement api

		}

		return transferList;
	}

}
