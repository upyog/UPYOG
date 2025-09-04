package org.egov.tl.repository.builder;

import java.util.List;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class UmeedDashboardQueryBuilder {

	private static final String UNIQUE_WARDS_SEARCH_QUERY = "SELECT DISTINCT additionaldetail->>'wardName' AS ward,"
			+ "additionaldetail->>'ulbName' AS ulb,additionaldetail->>'district' AS region "
			+ "FROM eg_tl_address WHERE TO_TIMESTAMP(createdtime / 1000)::date = TO_DATE(?, 'DD-MM-YYYY')";

	private static final String DATA_METRICS_SEARCH_QUERY = "SELECT "
			+ "COUNT(CASE WHEN TO_TIMESTAMP(tl.createdtime/1000)::DATE = TO_DATE(?,'DD-MM-YYYY') THEN 1 END) AS transactions, "
			+ "COUNT(CASE WHEN TO_TIMESTAMP(tl.issueddate/1000)::DATE = TO_DATE(?,'DD-MM-YYYY') AND "
			+ "(TO_TIMESTAMP(tl.issueddate/1000)::DATE - TO_TIMESTAMP(tl.createdtime/1000)::DATE) <= ? THEN 1 END) "
			+ "AS todaysLicenseIssuedWithinSLA, "
			+ "COUNT(CASE WHEN status = 'APPROVED' AND TO_TIMESTAMP(tl.issueddate/1000)::DATE = TO_DATE(?,'DD-MM-YYYY') "
			+ "THEN 1 END) AS todaysApprovedApplications, "
			+ "COUNT(CASE WHEN (status IS NULL OR status != 'APPROVED') AND "
			+ "(TO_DATE(?,'DD-MM-YYYY') - TO_TIMESTAMP(tl.createdtime/1000)::DATE) > ? THEN 1 END) "
			+ "AS pendingApplicationsBeyondTimeline, "
			+ "COUNT(CASE WHEN status = 'APPROVED' AND TO_TIMESTAMP(tl.issueddate/1000)::DATE = TO_DATE(?,'DD-MM-YYYY') "
			+ "AND (TO_TIMESTAMP(tl.issueddate/1000)::DATE - TO_TIMESTAMP(tl.createdtime/1000)::DATE) <= ? THEN 1 END) "
			+ "AS todaysApprovedApplicationsWithinSLA, "
			+ "AVG(TO_TIMESTAMP(tl.issueddate/1000)::DATE - TO_TIMESTAMP(tl.createdtime/1000)::DATE) AS avgDaysForApplicationApproval, "
			+ "? AS StipulatedDays "
			+ "FROM eg_tl_tradelicense tl JOIN eg_tl_tradelicensedetail dt ON tl.id = dt.tradelicenseid "
			+ "JOIN eg_tl_address addr ON addr.tradelicensedetailid = dt.id "
			+ "WHERE addr.additionaldetail->>'wardName' = ?";

	private static final String TODAYS_COLLECTION_SEARCH_QUERY = "SELECT dt.additionaldetail->>'tradeCategory' AS tradeType, "
			+ "COUNT(*) AS totalTransactions, SUM(eg_pg_transactions.txn_amount) AS totalTxnAmount "
			+ "FROM eg_tl_tradelicense tl JOIN eg_tl_tradelicensedetail dt ON tl.id = dt.tradelicenseid "
			+ "JOIN eg_tl_address addr ON addr.tradelicensedetailid = dt.id "
			+ "JOIN eg_pg_transactions ON eg_pg_transactions.consumer_code = tl.applicationnumber "
			+ "WHERE txn_status='SUCCESS' and addr.additionaldetail->>'wardName' = ? "

	private static final String TODAYS_TRADE_LICENSES_SEARCH_QUERY = "SELECT status, COUNT(CASE "
			+ "    WHEN TO_TIMESTAMP(tl.createdtime / 1000)::DATE = TO_DATE(?, 'DD-MM-YYYY') THEN 1 "
			+ "    ELSE NULL END) AS created_today_count, COUNT(CASE "
			+ "    WHEN TO_TIMESTAMP(tl.lastmodifiedtime / 1000)::DATE = TO_DATE(?, 'DD-MM-YYYY') THEN 1 "
			+ "    ELSE NULL END) AS modified_today_count FROM eg_tl_tradelicense tl "
			+ "JOIN eg_tl_tradelicensedetail dt ON tl.id = dt.tradelicenseid "
			+ "JOIN eg_tl_address addr ON addr.tradelicensedetailid = dt.id "
			+ "WHERE addr.additionaldetail->>'wardName' = ? GROUP BY status";

	public String getUniqueWardsSearchQuery(String stringDate, List<Object> preparedStmtList) {
		StringBuilder builder = new StringBuilder(UNIQUE_WARDS_SEARCH_QUERY);
		preparedStmtList.add(stringDate);

		return builder.toString();
	}

	public String getDataMetricsSearchQuery(String stringDate, String wardName, int slaDays,
			List<Object> preparedStmtList) {
		StringBuilder builder = new StringBuilder(DATA_METRICS_SEARCH_QUERY);
		// sequence should be same for preparedStmtList.add
		preparedStmtList.add(stringDate); // for transactions
		preparedStmtList.add(stringDate); // for todaysLicenseIssuedWithinSLA
		preparedStmtList.add(slaDays);
		preparedStmtList.add(stringDate); // for todaysApprovedApplications
		preparedStmtList.add(stringDate); // for pendingApplicationsBeyondTimeline
		preparedStmtList.add(slaDays);
		preparedStmtList.add(stringDate); // for todaysApprovedApplicationsWithinSLA
		preparedStmtList.add(slaDays);
		preparedStmtList.add(slaDays); // for StipulatedDays column
		preparedStmtList.add(wardName); // for WHERE clause

		return builder.toString();
	}

	public String getTodaysCollectionSearchQuery(String stringDate, String wardName, List<Object> preparedStmtList) {
		StringBuilder builder = new StringBuilder(TODAYS_COLLECTION_SEARCH_QUERY);
		// sequence should be same for preparedStmtList.add
		preparedStmtList.add(wardName);
		preparedStmtList.add(stringDate);

		return builder.toString();
	}

	public String getTodaysTradeLicensesSearchQuery(String stringDate, String wardName, List<Object> preparedStmtList) {
		StringBuilder builder = new StringBuilder(TODAYS_TRADE_LICENSES_SEARCH_QUERY);
		// sequence should be same for preparedStmtList.add
		preparedStmtList.add(stringDate); // for created_today_count date
		preparedStmtList.add(stringDate); // for modified_today_count date
		preparedStmtList.add(wardName); // for wardName

		return builder.toString();
	}

}
