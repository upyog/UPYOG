package org.egov.pg.service;

import java.util.List;

import javax.validation.Valid;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.User;
import org.egov.pg.models.Transaction;
import org.egov.pg.web.models.OpenTransactionRequest;
import org.egov.pg.web.models.TransactionRequestV2;
import org.egov.pg.web.models.TransactionResponseV2;
import org.egov.pg.web.models.UserSearchResponse;
import org.egov.pg.web.models.UserSearchResponseContent;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OpenTransactionService {

	@Autowired
	private UserService userService;

	@Autowired
	private TransactionServiceV2 transactionServiceV2;

	public List<Transaction> initiateOpenTransaction(@Valid OpenTransactionRequest openTransactionRequest) {

		if (!openTransactionRequest.isValid()) {
			throw new CustomException("INVALID REQUEST", "User uuid is missing.");
		}

		// validate user
		UserSearchResponse userSearchResponse = userService.searchUser(openTransactionRequest.getUserUuid());

		if (null == userSearchResponse || CollectionUtils.isEmpty(userSearchResponse.getUserSearchResponseContent())) {
			throw new CustomException("USER NOT FOUND", "User not found for given user uuid.");
		}

		UserSearchResponseContent userSearchResponseContent = userSearchResponse.getUserSearchResponseContent().get(0);

		// create transaction request
		TransactionRequestV2 transactionRequestV2 = TransactionRequestV2.builder().requestInfo(RequestInfo.builder()
				.userInfo(User.builder().id(userSearchResponseContent.getId()).uuid(userSearchResponseContent.getUuid())
						.name(userSearchResponseContent.getName()).userName(userSearchResponseContent.getUserName())
						.tenantId(userSearchResponseContent.getTenantId())
						.mobileNumber(userSearchResponseContent.getMobileNumber())
						.type(userSearchResponseContent.getType().name()).build())
				.build()).transactions(openTransactionRequest.getTransactions()).build();

		List<Transaction> transactions = transactionServiceV2.initiateTransaction(transactionRequestV2);

		return transactions;
	}

	public TransactionResponseV2 prepareResponse(List<Transaction> transactions) {
		return transactionServiceV2.prepareResponse(transactions, null);
	}

}
