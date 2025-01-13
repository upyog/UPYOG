package org.egov.pg.repository;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.egov.pg.web.models.TransactionDetailsCriteria;
import org.springframework.util.CollectionUtils;

class TransactionDetailsQueryBuilder {
//	private static final String SEARCH_TXN_SQL = "SELECT eptd.uuid, eptd.txn_id, eptd.txn_amount, eptd.bill_id, eptd.consumer_code, "
//			+ "eptd.created_by, eptd.created_time, eptd.last_modified_by, eptd.last_modified_time "
//			+ "FROM eg_pg_transactions_details eptd ";
	private static final String SEARCH_TXN_SQL = "SELECT * FROM eg_pg_transactions_details eptd ";

	private TransactionDetailsQueryBuilder() {
	}

	static String getTransactionDetails(TransactionDetailsCriteria transactionDetailsCriteria,
			List<Object> preparedStmtList) {
		String query = buildQuery(transactionDetailsCriteria, preparedStmtList);
		query = addOrderByClause(query);
		return query;
	}

	private static String buildQuery(TransactionDetailsCriteria transactionDetailsCriteria,
			List<Object> preparedStmtList) {
		StringBuilder builder = new StringBuilder(TransactionDetailsQueryBuilder.SEARCH_TXN_SQL);

		boolean isAppendAndClause = true;
		builder.append(" WHERE 1=1");

		if (!CollectionUtils.isEmpty(transactionDetailsCriteria.getTxnIds())) {
			isAppendAndClause = addAndClauseIfRequired(isAppendAndClause, builder);
			builder.append(" eptd.txn_id IN ( ").append(
					getQueryForCollection(new ArrayList<>(transactionDetailsCriteria.getTxnIds()), preparedStmtList))
					.append(" )");
		}
		if (!CollectionUtils.isEmpty(transactionDetailsCriteria.getBillIds())) {
			isAppendAndClause = addAndClauseIfRequired(isAppendAndClause, builder);
			builder.append(" eptd.bill_id IN ( ").append(
					getQueryForCollection(new ArrayList<>(transactionDetailsCriteria.getBillIds()), preparedStmtList))
					.append(" )");
		}
		if (!CollectionUtils.isEmpty(transactionDetailsCriteria.getConsumerCodes())) {
			isAppendAndClause = addAndClauseIfRequired(isAppendAndClause, builder);
			builder.append(" eptd.consumer_code IN ( ")
					.append(getQueryForCollection(new ArrayList<>(transactionDetailsCriteria.getConsumerCodes()),
							preparedStmtList))
					.append(" )");
		}

		return builder.toString();
	}

	private static boolean addAndClauseIfRequired(final boolean appendAndClauseFlag, final StringBuilder queryString) {
		if (appendAndClauseFlag)
			queryString.append(" AND");

		return true;
	}

	private static String getQueryForCollection(List<?> ids, List<Object> preparedStmtList) {
		StringBuilder builder = new StringBuilder();
		Iterator<?> iterator = ids.iterator();
		while (iterator.hasNext()) {
			builder.append(" ?");
			preparedStmtList.add(iterator.next());

			if (iterator.hasNext())
				builder.append(",");
		}
		return builder.toString();
	}

	private static String addOrderByClause(String query) {
		return query + " order by eptd.created_time desc ";
	}

}
