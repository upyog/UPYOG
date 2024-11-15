package org.egov.pqm.anomaly.finder.repository.querybuilder;

import java.util.List;

import org.egov.pqm.anomaly.finder.web.model.PqmAnomalySearchCriteria;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

@Component
public class AnomalyFinderQueryBuilder {

	private static final String ANOMALYFINDER_QUERY = "select * from eg_pqm_anomaly_details anomaly";

	public String anomalySearchQuery(List<String> testIdLists, List<Object> preparedStmtList) {
		StringBuilder builder = new StringBuilder(ANOMALYFINDER_QUERY);

		if (!CollectionUtils.isEmpty(testIdLists)) {
			addClauseIfRequired(preparedStmtList, builder);
			builder.append(" anomaly.testid IN (").append(createQuery(testIdLists)).append(")");
			addToPreparedStatement(preparedStmtList, testIdLists);
		}
		return builder.toString();
	}
	public String anomalySearchQueryWithCriteria(PqmAnomalySearchCriteria criteria, List<Object> preparedStmtList) {
		StringBuilder builder = new StringBuilder(ANOMALYFINDER_QUERY);

		List<String> testIds = criteria.getTestIds();
		if (!CollectionUtils.isEmpty(testIds)) {
			addClauseIfRequired(preparedStmtList, builder);
			builder.append(" anomaly.testid IN (").append(createQuery(testIds)).append(")");
			addToPreparedStatement(preparedStmtList,testIds);
		}

		if (criteria.getTenantId() != null) {
			if (criteria.getTenantId().split("\\.").length == 1) {
				addClauseIfRequired(preparedStmtList, builder);
				builder.append(" anomaly.tenantid like ?");
				preparedStmtList.add('%' + criteria.getTenantId() + '%');
			} else {
				addClauseIfRequired(preparedStmtList, builder);
				builder.append(" anomaly.tenantid=? ");
				preparedStmtList.add(criteria.getTenantId());
			}
		}

		return builder.toString();
	}

	private void addClauseIfRequired(List<Object> values, StringBuilder queryString) {
		if (values.isEmpty())
			queryString.append(" WHERE ");
		else {
			queryString.append(" AND");
		}
	}

	private void addToPreparedStatement(List<Object> preparedStmtList, List<String> ids) {
		ids.forEach(id -> {
			preparedStmtList.add(id);
		});

	}

	private Object createQuery(List<String> ids) {
		StringBuilder builder = new StringBuilder();
		int length = ids.size();
		for (int i = 0; i < length; i++) {
			builder.append(" ?");
			if (i != length - 1)
				builder.append(",");
		}
		return builder.toString();
	}
	
	public String getPqmAnomalyLikeQuery(PqmAnomalySearchCriteria criteria, List<Object> preparedStmtList) {

		StringBuilder builder = new StringBuilder(ANOMALYFINDER_QUERY);

		List<String> ids = criteria.getIds();
		if (!CollectionUtils.isEmpty(ids)) {

			addClauseIfRequired(preparedStmtList, builder);
			builder.append(" anomaly.id IN (").append(createQuery(ids)).append(")");
			addToPreparedStatement(preparedStmtList, ids);
		}

		return addPaginationClause(builder, preparedStmtList, criteria);

	}
	
	private String addPaginationClause(StringBuilder builder, List<Object> preparedStmtList,
			PqmAnomalySearchCriteria criteria) {

		if (criteria.getLimit() != null && criteria.getLimit() != 0) {
			builder.append(
					"and anomaly.id in (select id from eg_pqm_anomaly_details where tenantid= ? order by id offset ? limit ?)");
			preparedStmtList.add(criteria.getTenantId());
			preparedStmtList.add(criteria.getOffset());
			preparedStmtList.add(criteria.getLimit());

			addOrderByClause(builder, criteria);

		} else {
			addOrderByClause(builder, criteria);
		}
		return builder.toString();
	}
	
	/**
	 * 
	 * @param builder
	 * @param criteria
	 */
	private void addOrderByClause(StringBuilder builder, PqmAnomalySearchCriteria criteria) {
		
			builder.append(" ORDER BY anomaly.id DESC ");

	}

}
