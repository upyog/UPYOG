/*
 * eGov suite of products aim to improve the internal efficiency,transparency,
 *    accountability and the service delivery of the government  organizations.
 *
 *     Copyright (C) <2015>  eGovernments Foundation
 *
 *     The updated version of eGov suite of products as by eGovernments Foundation
 *     is available at http://www.egovernments.org
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program. If not, see http://www.gnu.org/licenses/ or
 *     http://www.gnu.org/licenses/gpl.html .
 *
 *     In addition to the terms of the GPL license to be adhered to in using this
 *     program, the following additional terms are to be complied with:
 *
 *         1) All versions of this program, verbatim or modified must carry this
 *            Legal Notice.
 *
 *         2) Any misrepresentation of the origin of the material is prohibited. It
 *            is required that all modified versions of this material be marked in
 *            reasonable ways as different from the original version.
 *
 *         3) This license does not grant any rights to any user of the program
 *            with regards to rights under trademark law for use of the trade names
 *            or trademarks of eGovernments Foundation.
 *
 *   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 */
package org.egov.demand.repository.querybuilder;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.egov.demand.model.TaxPeriod;
import org.egov.demand.model.enums.PeriodCycle;
import org.egov.demand.repository.TaxPeriodRepository;
import org.egov.demand.web.contract.TaxPeriodCriteria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class TaxPeriodQueryBuilder {

	private static final Logger logger = LoggerFactory.getLogger(TaxPeriodRepository.class);

	private static final String BASE_QUERY = "SELECT * FROM EGBS_TAXPERIOD taxperiod ";

	public final String insertQuery = "INSERT INTO public.egbs_taxperiod(id, service, code, fromdate, todate,"
			+ " financialyear, createddate,lastmodifieddate, createdby, lastmodifiedby, tenantid, periodcycle)"
			+ " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

	public final String updateQuery = "UPDATE public.egbs_taxperiod SET service=?, code=?, fromdate=?, todate=?,"
			+ " financialyear=?, lastmodifieddate=?, lastmodifiedby=?, tenantid=?,"
			+ " periodcycle=? WHERE tenantid = ? and id = ? ";

	public final String PERIOD_VALIDATE_QUERY = "SELECT * FROM egbs_taxperiod WHERE tenantId= ? AND service= ?"
			+ " AND ( ? BETWEEN fromdate AND todate OR ? BETWEEN fromdate AND todate)"
			+ " OR (fromdate BETWEEN ? AND ? OR todate BETWEEN ? AND ?);";

	public String prepareSearchQuery(final TaxPeriodCriteria taxPeriodCriteria, final List preparedStatementValues) {
		final StringBuilder selectQuery = new StringBuilder(BASE_QUERY);
		logger.info("prepareSearchQuery --> ");
		prepareWhereClause(selectQuery, preparedStatementValues, taxPeriodCriteria);
		logger.info("Search tax periods query from TaxPeriodQueryBuilder -> " + selectQuery);
		return selectQuery.toString();
	}

	private void prepareWhereClause(final StringBuilder selectQuery, final List<Object> preparedStatementValues,
			final TaxPeriodCriteria taxPeriodCriteria) {

		String tenantId = taxPeriodCriteria.getTenantId();
		selectQuery.append(" WHERE ");

		if (StringUtils.isNotBlank(tenantId)) {
			selectQuery.append(" taxperiod.tenantId = ? ");
			preparedStatementValues.add(taxPeriodCriteria.getTenantId());
		}

		Set<String> service = taxPeriodCriteria.getService();
		if (service != null && !service.isEmpty()) {
			selectQuery.append(" and taxperiod.service IN " + getQueryForCollection(service,preparedStatementValues));
		}

		PeriodCycle periodCycle = taxPeriodCriteria.getPeriodCycle();
		if (periodCycle != null) {
			selectQuery.append(" and taxperiod.periodcycle = ? ");
			preparedStatementValues.add(periodCycle.toString());
		}

		if (taxPeriodCriteria.getFromDate() != null && taxPeriodCriteria.getToDate() != null) {
			if (service != null && !service.isEmpty() && service.size() == 1 && periodCycle != null) {
				selectQuery.append(
						" AND (fromdate >=  CASE WHEN ((SELECT fromdate FROM egbs_taxperiod WHERE tenantId =? AND ( ? BETWEEN fromdate AND  todate)  "
								+ " AND service IN " + getQueryForCollection(service,preparedStatementValues) + " AND periodcycle=?) NOTNULL) "
								+ "THEN "
								+ "( SELECT fromdate FROM egbs_taxperiod WHERE tenantId =? AND ( ? BETWEEN fromdate AND  todate)"
								+ " AND service IN " + getQueryForCollection(service,preparedStatementValues) + " AND periodcycle=?) " + "ELSE "
								+ "(SELECT min(fromdate) FROM egbs_taxperiod WHERE tenantId =?)" + " END"
								+ " AND todate <= ( SELECT todate FROM egbs_taxperiod WHERE tenantId = ? AND (? BETWEEN fromdate AND  todate) "
								+ " AND service IN " + getQueryForCollection(service,preparedStatementValues) + " AND periodcycle=?))");

				preparedStatementValues.add(tenantId);
				preparedStatementValues.add(taxPeriodCriteria.getFromDate());
				preparedStatementValues.add(periodCycle.toString());
				preparedStatementValues.add(tenantId);
				preparedStatementValues.add(taxPeriodCriteria.getFromDate());
				preparedStatementValues.add(periodCycle.toString());
				preparedStatementValues.add(tenantId);
				preparedStatementValues.add(tenantId);
				preparedStatementValues.add(taxPeriodCriteria.getToDate());
				preparedStatementValues.add(periodCycle.toString());
			} else {
				if (taxPeriodCriteria.getFromDate() != null) {
					selectQuery.append(" and taxperiod.fromdate >= ? ");
					preparedStatementValues.add(taxPeriodCriteria.getFromDate());
				}
				if (taxPeriodCriteria.getToDate() != null) {
					selectQuery.append(" and taxperiod.todate <= ? ");
					preparedStatementValues.add(taxPeriodCriteria.getToDate());
				}
			}
		}

		if (StringUtils.isNotBlank(taxPeriodCriteria.getCode())) {
			selectQuery.append(" and taxperiod.code = ? ");
			preparedStatementValues.add(taxPeriodCriteria.getCode());
		}

		Set<String> ids = taxPeriodCriteria.getId();
		if (ids != null && !ids.isEmpty())
			selectQuery.append(" and taxperiod.id IN " + getQueryForCollection(ids,preparedStatementValues));

		if (taxPeriodCriteria.getDate() != null) {
			selectQuery.append(" and taxperiod.fromdate <= ? and taxperiod.todate >= ? ");
			preparedStatementValues.add(taxPeriodCriteria.getDate());
			preparedStatementValues.add(taxPeriodCriteria.getDate());
		}
	}

	public String prepareQueryForValidation(List<TaxPeriod> taxPeriodList, String mode,
			List<Object> preparedStatementValues) {

		String baseQuery = "select exists (select * from egbs_taxperiod taxperiod where ";

		StringBuilder whereClause = new StringBuilder();

		int count = 0;

		for (TaxPeriod taxPeriod : taxPeriodList) {

			whereClause.append(" ( ");

			if (StringUtils.isNotBlank(taxPeriod.getService())) {
				whereClause.append(" taxperiod.service = ? AND ");
				preparedStatementValues.add(taxPeriod.getService());
			}

			if (StringUtils.isNotBlank(taxPeriod.getCode())) {
				whereClause.append(" taxperiod.code = ? AND ");
				preparedStatementValues.add(taxPeriod.getCode());
			}

			if ("edit".equalsIgnoreCase(mode)) {
				whereClause.append(" taxperiod.id != ? AND ");
				preparedStatementValues.add(taxPeriod.getId());
			}

			if (StringUtils.isNotBlank(taxPeriod.getTenantId())) {
				whereClause.append(" taxperiod.tenantId = ? AND ");
				preparedStatementValues.add(taxPeriod.getTenantId());
			}

			if (taxPeriod.getFromDate() != null && taxPeriod.getToDate() != null) {

				whereClause.append(" ( ? BETWEEN taxperiod.fromdate AND taxperiod.todate "
						+ " OR ? BETWEEN taxperiod.fromdate AND taxperiod.todate "
						+ " OR taxperiod.fromdate BETWEEN ? AND ? " + " OR taxperiod.todate BETWEEN ? AND ? ) ");

				preparedStatementValues.add(taxPeriod.getFromDate());
				preparedStatementValues.add(taxPeriod.getToDate());
				preparedStatementValues.add(taxPeriod.getFromDate());
				preparedStatementValues.add(taxPeriod.getToDate());
				preparedStatementValues.add(taxPeriod.getFromDate());
				preparedStatementValues.add(taxPeriod.getToDate());
			}

			whereClause.append(" ) ");

			count++;

			if (taxPeriodList.size() > count) {
				whereClause.append(" OR ");
			}
		}

		return baseQuery.concat(whereClause.toString()).concat(" )");
	}
	
	
	private String getQueryForCollection(Set<String> values, List<Object> preparedStatementValues) {

		StringBuilder query = new StringBuilder("(");

		int index = 0;

		for (String value : values) {
			if (index > 0) {
				query.append(",");
			}

			query.append("?");
			preparedStatementValues.add(value);
			index++;
		}

		return query.append(")").toString();
	}
}
