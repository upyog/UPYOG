package org.egov.pt.calculator.repository.querybuilder;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.egov.pt.calculator.web.models.MutationBillingSlabSearchCriteria;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

@Component
public class MutationBillingSlabQueryBuilder {
	
	private static final String ALL = "ALL";
	@Value("${mn.mutation.fee.calculate.type}")
	private String mutationCalculateType;

    public String getBillingSlabSearchQuery(MutationBillingSlabSearchCriteria billingSlabSearcCriteria,
                                            List<Object> preparedStmtList) {

        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("SELECT * FROM eg_pt_mutation_billingslab");
        addWhereClause(queryBuilder, billingSlabSearcCriteria, preparedStmtList);
        return queryBuilder.toString();
    }

    public void addWhereClause(StringBuilder queryBuilder, MutationBillingSlabSearchCriteria billingSlabSearcCriteria,
                               List<Object> preparedStmtList) {

        queryBuilder.append(" WHERE tenantId = ?");

        preparedStmtList.add(billingSlabSearcCriteria.getTenantId());

        List<String> ids = billingSlabSearcCriteria.getId();

        if (!CollectionUtils.isEmpty(ids)) {

            queryBuilder.append(" AND id IN ( ");
            setValuesForList(queryBuilder, preparedStmtList, ids);
            queryBuilder.append(")");
        }

        if (!StringUtils.isEmpty(billingSlabSearcCriteria.getPropertyType())) {

            queryBuilder.append(" AND propertyType = ?");
            preparedStmtList.add(billingSlabSearcCriteria.getPropertyType());
        }
        
        if(!mutationCalculateType.equalsIgnoreCase(ALL)) {
        	
        	 if (!StringUtils.isEmpty(billingSlabSearcCriteria.getPropertySubType())) {

                 queryBuilder.append(" AND propertySubType = ?");
                 preparedStmtList.add(billingSlabSearcCriteria.getPropertySubType());
             }

             if (!StringUtils.isEmpty(billingSlabSearcCriteria.getUsageCategoryMajor())) {

                 queryBuilder.append(" AND usageCategoryMajor = ?");
                 preparedStmtList.add(billingSlabSearcCriteria.getUsageCategoryMajor());
             }

             if (!StringUtils.isEmpty(billingSlabSearcCriteria.getUsageCategoryMinor())) {

                 queryBuilder.append(" AND usageCategoryMinor = ?");
                 preparedStmtList.add(billingSlabSearcCriteria.getUsageCategoryMinor());
             }

             if (!StringUtils.isEmpty(billingSlabSearcCriteria.getOwnerShipCategory())) {

                 queryBuilder.append(" AND ownerShipCategory = ?");
                 preparedStmtList.add(billingSlabSearcCriteria.getOwnerShipCategory());
             }

             if (!StringUtils.isEmpty(billingSlabSearcCriteria.getSubOwnerShipCategory())) {

                 queryBuilder.append(" AND subOwnerShipCategory = ?");
                 preparedStmtList.add(billingSlabSearcCriteria.getSubOwnerShipCategory());
             }
        }else {
        	 if (!StringUtils.isEmpty(billingSlabSearcCriteria.getPropertySubType())) {

                 queryBuilder.append(" AND propertySubType = ?");
                 preparedStmtList.add(ALL);
             }

             if (!StringUtils.isEmpty(billingSlabSearcCriteria.getUsageCategoryMajor())) {

                 queryBuilder.append(" AND usageCategoryMajor = ?");
                 preparedStmtList.add(ALL);
             }

             if (!StringUtils.isEmpty(billingSlabSearcCriteria.getUsageCategoryMinor())) {

                 queryBuilder.append(" AND usageCategoryMinor = ?");
                 preparedStmtList.add(ALL);
             }

             if (!StringUtils.isEmpty(billingSlabSearcCriteria.getOwnerShipCategory())) {

                 queryBuilder.append(" AND ownerShipCategory = ?");
                 preparedStmtList.add(ALL);
             }

             if (!StringUtils.isEmpty(billingSlabSearcCriteria.getSubOwnerShipCategory())) {

                 queryBuilder.append(" AND subOwnerShipCategory = ?");
                 preparedStmtList.add(ALL);
             }
        }

       
        if (billingSlabSearcCriteria.getMarketValue() != null) {

            queryBuilder.append(" AND minMarketValue <= ?");
            preparedStmtList.add(billingSlabSearcCriteria.getMarketValue());
            queryBuilder.append(" AND maxMarketValue >= ?");
            preparedStmtList.add(billingSlabSearcCriteria.getMarketValue());
        }
    }

    /**
     * sets prepared statement for values for a list
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
