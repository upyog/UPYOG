package org.egov.tlcalculator.repository.builder;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.egov.tlcalculator.web.models.BillingSlabSearchCriteria;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class BillingslabQueryBuilder {

	/**
	 * Builds search query for searching billing slabs from db
	 * 
	 * @param criteria
	 * @param preparedStmtList
	 * @return
	 */
	public String getSearchQuery(BillingSlabSearchCriteria criteria, List<Object> preparedStmtList) {
		StringBuilder queryBuilder = new StringBuilder();
		queryBuilder.append("SELECT * from eg_tl_billingslab ");
		addWhereClause(queryBuilder, criteria, preparedStmtList);
		return queryBuilder.toString();
	}

	/**
	 * Builds the where clause for the search query.
	 * 
	 * @param queryBuilder
	 * @param billingSlabSearcCriteria
	 * @param preparedStmtList
	 */
	public void addWhereClause(StringBuilder queryBuilder, BillingSlabSearchCriteria billingSlabSearcCriteria,
			List<Object> preparedStmtList) {
		queryBuilder.append(" WHERE tenantid = ?");
		preparedStmtList.add(billingSlabSearcCriteria.getTenantId().split("\\.")[0]);
		// System.out.println("TenantID:" +
		// billingSlabSearcCriteria.getTenantId().split("\\.")[0]);
		List<String> ids = billingSlabSearcCriteria.getIds();
		if (!CollectionUtils.isEmpty(ids)) {
			queryBuilder.append(" AND id IN ( ");
			setValuesForList(queryBuilder, preparedStmtList, ids);
			queryBuilder.append(")");
		}
		if (!StringUtils.isEmpty(billingSlabSearcCriteria.getLicenseType())) {
			queryBuilder.append(" AND licensetype = 'PERMANENT'");
		}
		if (!StringUtils.isEmpty(billingSlabSearcCriteria.getApplicationType())) {
			queryBuilder.append(" AND applicationtype = 'TL' ");
		}
		if (!StringUtils.isEmpty(billingSlabSearcCriteria.getStructureType())) {
			queryBuilder.append(" AND  structuretype = 'NA'");
		}
		if (!StringUtils.isEmpty(billingSlabSearcCriteria.getTradeType())) {
			queryBuilder.append(" AND tradetype = 'ALL'");
		}
		if (null != billingSlabSearcCriteria.getEnterpriseType()) {
			queryBuilder.append(" AND (enterprisetype =? )");
			preparedStmtList.add(billingSlabSearcCriteria.getEnterpriseType());
		}
	}

	/**
	 * Sets prepared statement for values for a list
	 * 
	 * @param queryBuilder
	 * @param preparedStmtList
	 * @param ids
	 */
	private void setValuesForList(StringBuilder queryBuilder, List<Object> preparedStmtList, List<String> ids) {
		int len = ids.size();
		for (int i = 0; i < ids.size(); i++) {
			queryBuilder.append("?");
			if (i != len - 1)
				queryBuilder.append(", ");
			preparedStmtList.add(ids.get(i));
		}
	}

}
