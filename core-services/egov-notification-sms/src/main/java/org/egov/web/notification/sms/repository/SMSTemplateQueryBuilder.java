package org.egov.web.notification.sms.repository;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.egov.web.notification.sms.models.SMSTemplateSearchCriteria;
import org.springframework.util.CollectionUtils;

public class SMSTemplateQueryBuilder {

	private static final String SEARCH_SMS_TEMPLATE_SQL = "SELECT * FROM eg_sms_template est ";

	public static String getSmsTemplate(SMSTemplateSearchCriteria smsTemplateSearchCriteria,
			List<Object> preparedStmtList) {
		String query = buildQuery(smsTemplateSearchCriteria, preparedStmtList);
		query = addOrderByClause(query);
		return query;
	}

	private static String buildQuery(SMSTemplateSearchCriteria smsTemplateSearchCriteria,
			List<Object> preparedStmtList) {
		StringBuilder builder = new StringBuilder(SEARCH_SMS_TEMPLATE_SQL);

		boolean isAppendAndClause = true;
		builder.append(" WHERE 1=1");

		if (!CollectionUtils.isEmpty(smsTemplateSearchCriteria.getTemplateNames())) {
			isAppendAndClause = addAndClauseIfRequired(isAppendAndClause, builder);
			builder.append(" est.template_name IN ( ")
					.append(getQueryForCollection(new ArrayList<>(smsTemplateSearchCriteria.getTemplateNames()),
							preparedStmtList))
					.append(" )");
		}

		// fetch only active record
		isAppendAndClause = addAndClauseIfRequired(isAppendAndClause, builder);
		builder.append(" is_active = true");

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
		return query + " order by est.createddate desc ";
	}
	
	
	
	 public static final String INSERT_SMS_TRACKER = 
		        "INSERT INTO eg_notification_sms_tracker (" +
		        "uuid, amount, application_no, tenant_id, service, month, year, financial_year, " +
		        "from_date, to_date, created_by, created_time, last_modified_by, last_modified_time, " +
		        "ward, bill_id, additional_detail, sms_status, sms_request, sms_response, owner_mobile_no, owner_name" +
		        ") VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	

}
