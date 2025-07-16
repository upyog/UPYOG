package org.egov.waterquality.repository.builder;

import java.util.List;
import java.util.Set;

import org.egov.common.contract.request.RequestInfo;
import org.egov.waterquality.config.WSConfiguration;
import org.egov.waterquality.web.models.collection.SearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

@Component
public class WsQueryBuilder {

	@Autowired
	private WSConfiguration config;
	
	private static final String INNER_JOIN_STRING = "INNER JOIN";
    private static final String LEFT_OUTER_JOIN_STRING = " LEFT OUTER JOIN ";
//	private static final String Offset_Limit_String = "OFFSET ? LIMIT ?";
    
    private static final String WATER_QUALITY_APPLICATION_SEARCH = "";
    
	private static final String PAGINATION_WRAPPER = "SELECT * FROM " +
            "(SELECT *, DENSE_RANK() OVER (ORDER BY wc_appCreatedDate DESC) offset_ FROM " +
            "({})" +
            " result) result_offset " +
            "WHERE offset_ > ? AND offset_ <= ?";
	
	private static final String COUNT_WRAPPER = " SELECT COUNT(*) FROM ({INTERNAL_QUERY}) AS count ";

	private static final String ORDER_BY_CLAUSE= " ORDER BY wc.appCreatedDate DESC";
	
	private static final String ORDER_BY_COUNT_CLAUSE= " ORDER BY appCreatedDate DESC";

	private static final String LATEST_EXECUTED_MIGRATION_QUERY = "select * from {schema}.eg_ws_enc_audit where tenantid = ? order by createdTime desc limit 1;";

	/**
	 * 
	 * @param criteria
	 *            The WaterCriteria
	 * @param preparedStatement
	 *            The Array Of Object
	 * @param requestInfo
	 *            The Request Info
	 * @return query as a string
	 */
	public String getSearchQueryString(SearchCriteria criteria, List<Object> preparedStatement,
			RequestInfo requestInfo) {

		StringBuilder query = new StringBuilder(WATER_QUALITY_APPLICATION_SEARCH);

		if (!StringUtils.isEmpty(criteria.getTenantId())) {
			addClauseIfRequired(preparedStatement, query);
			query.append(" tenantid = ? ");
			preparedStatement.add(criteria.getTenantId());
		}
		
		if (!CollectionUtils.isEmpty(criteria.getApplicationNo())) {
			addClauseIfRequired(preparedStatement, query);
			query.append(" applicationno in (").append(createQuery(criteria.getApplicationNo())).append(" )");
			addToPreparedStatement(preparedStatement, criteria.getApplicationNo());
		}
		
		if (!CollectionUtils.isEmpty(criteria.getIds())) {
			addClauseIfRequired(preparedStatement, query);
			query.append(" id in (").append(createQuery(criteria.getIds())).append(" )");
			addToPreparedStatement(preparedStatement, criteria.getIds());
		}
		
		if (!StringUtils.isEmpty(criteria.getType())) {
			addClauseIfRequired(preparedStatement, query);
			query.append(" type = ? ");
			preparedStatement.add(criteria.getType());
		}
		
		query.append(ORDER_BY_CLAUSE);
		String finalQuery = addPaginationWrapper(query.toString(), preparedStatement, criteria);

		return finalQuery;		
	}
	
	
	private void addClauseIfRequired(List<Object> values, StringBuilder queryString) {
		if (values.isEmpty())
			queryString.append(" WHERE ");
		else {
			queryString.append(" AND");
		}
	}

	private String createQuery(Set<String> ids) {
		StringBuilder builder = new StringBuilder();
		int length = ids.size();
		for (int i = 0; i < length; i++) {
			builder.append(" ?");
			if (i != length - 1)
				builder.append(",");
		}
		return builder.toString();
	}

	private void addToPreparedStatement(List<Object> preparedStatement, Set<String> ids) {
		preparedStatement.addAll(ids);
	}


	/**
	 * 
	 * @param query
	 *            Query String
	 * @param preparedStmtList
	 *            Array of object for preparedStatement list
	 * @param criteria SearchCriteria
	 * @return It's returns query
	 */
	private String addPaginationWrapper(String query, List<Object> preparedStmtList, SearchCriteria criteria) {
		Integer limit = config.getDefaultLimit();
		Integer offset = config.getDefaultOffset();
		if (criteria.getLimit() == null && criteria.getOffset() == null)
			limit = config.getMaxLimit();

		if (criteria.getLimit() != null && criteria.getLimit() <= config.getDefaultLimit())
			limit = criteria.getLimit();

		if (criteria.getLimit() != null && criteria.getLimit() > config.getDefaultLimit())
			limit = config.getDefaultLimit();

		if (criteria.getOffset() != null)
			offset = criteria.getOffset();

		preparedStmtList.add(offset);
		preparedStmtList.add(limit + offset);
		return PAGINATION_WRAPPER.replace("{}",query);
	}
	
	private void addORClauseIfRequired(List<Object> values, StringBuilder queryString){
		if (values.isEmpty())
			queryString.append(" WHERE ");
		else {
			queryString.append(" OR");
		}
	}

}