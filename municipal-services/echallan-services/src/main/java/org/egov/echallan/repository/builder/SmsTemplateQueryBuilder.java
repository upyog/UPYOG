package org.egov.echallan.repository.builder;

import lombok.extern.slf4j.Slf4j;

import org.egov.echallan.config.ChallanConfiguration;
import org.egov.echallan.model.SearchCriteria;
//import org.egov.web.notification.sms.models.SMSTemplateSearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Slf4j
@Component
public class SmsTemplateQueryBuilder {

    private ChallanConfiguration config;

    @Autowired
    public SmsTemplateQueryBuilder(ChallanConfiguration config) {
        this.config = config;
    }

	private static final String SEARCH_SMS_TEMPLATE_SQL = "SELECT * FROM eg_sms_template est ";


	public static String getSmsTemplate(String TemaplateName,List<Object> preparedStmtList) {
		String query = buildQuery(TemaplateName,preparedStmtList);
//		query = addOrderByClause(query);
		return query;
	}
	
	private static String buildQuery(String temaplateName,List<Object> preparedStmtList) {
		StringBuilder builder = new StringBuilder(SEARCH_SMS_TEMPLATE_SQL);

		boolean isAppendAndClause = true;
		builder.append(" WHERE 1=1");

		if (temaplateName !=null) {
			isAppendAndClause = addAndClauseIfRequired(isAppendAndClause, builder);
			builder.append(" est.template_name = ?");
            preparedStmtList.add("ECHALLAN-CREATE");
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
	
	
	private static String addOrderByClause(String query) {
		return query + " order by est.createddate desc ";
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




}
