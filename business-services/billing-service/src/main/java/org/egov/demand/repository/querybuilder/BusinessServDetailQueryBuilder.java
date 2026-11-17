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

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.egov.demand.model.BusinessServiceDetail;
import org.egov.demand.web.contract.BusinessServiceDetailCriteria;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
public class BusinessServDetailQueryBuilder {

    private static final String BASE_QUERY = "SELECT * FROM EGBS_BUSINESS_SERVICE_DETAILS businessservice ";

    public String prepareSearchQuery(final BusinessServiceDetailCriteria businessServiceDetailCriteria, final List<Object> preparedStatementValues) {
        final StringBuilder selectQuery = new StringBuilder(BASE_QUERY);
        log.debug("prepareSearchQuery --> ");
        prepareWhereClause(selectQuery, preparedStatementValues, businessServiceDetailCriteria);
        log.debug("Search business service details query from BusinessServDetailQueryBuilder -> " + selectQuery);
        return selectQuery.toString();
    }

    private void prepareWhereClause(final StringBuilder selectQuery, final List<Object> preparedStatementValues,
                                    final BusinessServiceDetailCriteria businessServiceDetailCriteria) {

    	boolean hasCondition = false;

        if (StringUtils.isNotBlank(businessServiceDetailCriteria.getTenantId())) {
        	selectQuery.append(addClauseOnFilter(hasCondition)).append(" businessservice.tenantId = ? ");
			preparedStatementValues.add(businessServiceDetailCriteria.getTenantId());
			hasCondition = true;
        }

        if (!CollectionUtils.isEmpty(businessServiceDetailCriteria.getBusinessService())) {
        	selectQuery.append(addClauseOnFilter(hasCondition)).append(" businessservice.businessservice IN ").append(getQueryForCollection(businessServiceDetailCriteria.getBusinessService(), preparedStatementValues));
			hasCondition = true;
		}

        if (businessServiceDetailCriteria.getId() != null && !businessServiceDetailCriteria.getId().isEmpty())
        	selectQuery.append(addClauseOnFilter(hasCondition)).append(" businessservice.id IN ").append(getQueryForCollection(businessServiceDetailCriteria.getId(), preparedStatementValues));

    }

    public String prepareQueryForValidation(List<BusinessServiceDetail> businessServiceDetailList, String mode,List<Object> preparedStatementValues) {
    	StringBuilder query = new StringBuilder("SELECT EXISTS (SELECT 1 FROM EGBS_BUSINESS_SERVICE_DETAILS businessservice WHERE ");
        if (CollectionUtils.isEmpty(businessServiceDetailList)) {
			return query.append("1 = 0)").toString();
		}
        for (int i = 0; i < businessServiceDetailList.size(); i++) {
			BusinessServiceDetail detail = businessServiceDetailList.get(i);
			if (i > 0) {
				query.append(" OR ");
			}
			query.append(" ( ");
			boolean hasCondition = false;
			if (StringUtils.isNotBlank(detail.getBusinessService())) {
				query.append(addClauseOnFilter(hasCondition)).append(" businessservice.businessservice = ?");
				preparedStatementValues.add(detail.getBusinessService());
				hasCondition = true;
			}

			if ("edit".equalsIgnoreCase(mode)) {
				query.append(addClauseOnFilter(hasCondition)).append("businessservice.id != ?");
				preparedStatementValues.add(detail.getId());
				hasCondition = true;
			}

			if (StringUtils.isNotBlank(detail.getTenantId())) {
				query.append(addClauseOnFilter(hasCondition)).append("businessservice.tenantId = ?");
				preparedStatementValues.add(detail.getTenantId());
				hasCondition = true;
			}
		
			if (!hasCondition) {
				query.append("1 = 0");
			}
			query.append(" )");
		}
        return query.append(" )").toString();
    }

    public String getInsertQuery() {
        return "INSERT INTO EGBS_BUSINESS_SERVICE_DETAILS(id,businessservice,collectionModesNotAllowed,partPaymentAllowed,callBackForApportioning," +
                "callBackApportionURL,createddate,lastmodifieddate,createdby,lastmodifiedby,tenantid) " +
                "values (?,?,?,?,?,?,?,?,?,?,?);";
    }

    public String getUpdateQuery() {
        return "UPDATE EGBS_BUSINESS_SERVICE_DETAILS SET businessservice = ?, collectionModesNotAllowed = ?, partPaymentAllowed = ?, " +
                "callBackForApportioning = ?, callBackApportionURL = ?, lastmodifieddate = ?, lastmodifiedby = ? " +
                "WHERE tenantid = ? and id = ?;";
    }

    private String getQueryForCollection(Set<String> values, List<Object> preparedStatementValues) {
		String placeholders = values.stream().map(value -> "?").collect(Collectors.joining(","));
		preparedStatementValues.addAll(values);
		return "(" + placeholders + ")";
	}
    
    private String addClauseOnFilter(boolean hasCondition) {
        return hasCondition ? " AND " : " WHERE ";
    }
}