package org.egov.pqm.repository.querybuilder;

import static org.egov.pqm.util.QueryBuilderUtil.addParamsToQuery;
import static org.egov.pqm.util.QueryBuilderUtil.addToPreparedStatement;
import static org.egov.pqm.util.QueryBuilderUtil.addToWhereClause;

import java.util.List;
import org.egov.pqm.config.ServiceConfiguration;
import org.egov.pqm.web.model.Pagination;
import org.egov.pqm.web.model.SortBy;
import org.egov.pqm.web.model.plant.user.PlantUserSearchCriteria;
import org.egov.pqm.web.model.plant.user.PlantUserSearchRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

@Component
public class PlantUserQueryBuilder {

    private static final String QUERY =
            "SELECT COUNT(*) OVER() AS total_count, plant_user.* FROM eq_plant_user_map plant_user";
    private static final String PAGINATION_WRAPPER = "{} {order_by} {pagination}";

    @Autowired
    private ServiceConfiguration serviceConfiguration;

    public String getSearchQuery(PlantUserSearchRequest plantUserSearchRequest, List<Object> preparedStmtList) {
        PlantUserSearchCriteria plantUserSearchCriteria = plantUserSearchRequest.getPlantUserSearchCriteria();
        StringBuilder queryBuilder = new StringBuilder(QUERY);

        // Tenant
        if (plantUserSearchCriteria.getTenantId() != null
                && (plantUserSearchCriteria.getTenantId().split("\\.").length == 1)) {
            addToWhereClause(preparedStmtList, queryBuilder);
            queryBuilder.append(" plant_user.tenantid like ?");
            preparedStmtList.add('%' + plantUserSearchCriteria.getTenantId() + '%');
        } else if (plantUserSearchCriteria.getTenantId() != null) {
            addToWhereClause(preparedStmtList, queryBuilder);
            queryBuilder.append(" plant_user.tenantid=? ");
            preparedStmtList.add(plantUserSearchCriteria.getTenantId());
        }

        // Ids
        List<String> ids = plantUserSearchCriteria.getIds();
        if (!CollectionUtils.isEmpty(ids)) {
            addToWhereClause(preparedStmtList, queryBuilder);
            queryBuilder.append(" plant_user.id IN (").append(addParamsToQuery(ids)).append(")");
            addToPreparedStatement(preparedStmtList, ids);
        }

        List<String> plantCodes = plantUserSearchCriteria.getPlantCodes();
        if (!CollectionUtils.isEmpty(plantCodes)) {
            addToWhereClause(preparedStmtList, queryBuilder);
            queryBuilder.append(" plant_user.plantCode IN (").append(addParamsToQuery(plantCodes)).append(")");
            addToPreparedStatement(preparedStmtList, plantCodes);
        }

        List<String> plantUserUuids = plantUserSearchCriteria.getPlantUserUuids();
        if (!CollectionUtils.isEmpty(plantUserUuids)) {
            addToWhereClause(preparedStmtList, queryBuilder);
            queryBuilder.append(" plant_user.plantUserUuid IN (").append(addParamsToQuery(plantUserUuids)).append(")");
            addToPreparedStatement(preparedStmtList, plantUserUuids);
        }
		if (plantUserSearchCriteria.getIsActive() != null) {
			addToWhereClause(preparedStmtList, queryBuilder);
			queryBuilder.append(" plant_user.isActive=? ");
			preparedStmtList.add(plantUserSearchCriteria.getIsActive());

		} 

        List<String> plantUserTypes = plantUserSearchCriteria.getPlantUserTypes();
        if (!CollectionUtils.isEmpty(plantUserTypes)) {
            addToWhereClause(preparedStmtList, queryBuilder);
            queryBuilder.append(" plant_user.plantUserType IN (").append(addParamsToQuery(plantUserTypes)).append(")");
            addToPreparedStatement(preparedStmtList, plantUserTypes);
        }

        return addPaginationWrapper(queryBuilder.toString(), preparedStmtList, plantUserSearchRequest.getPagination());
    }

    /**
     * @param query            prepared Query
     * @param preparedStmtList values to be replaced on the query
     * @param pagination       pagination
     * @return the query by replacing the placeholders with preparedStmtList
     */
    private String addPaginationWrapper(
            String query, List<Object> preparedStmtList, Pagination pagination) {

        int limit = serviceConfiguration.getDefaultLimit();
        int offset = serviceConfiguration.getDefaultOffset();
        String finalQuery = PAGINATION_WRAPPER.replace("{}", query);

        if (pagination != null && pagination.getLimit() != null && pagination.getLimit() <= serviceConfiguration.getMaxSearchLimit())
            limit = pagination.getLimit();

        if (pagination != null && pagination.getLimit() != null && pagination.getLimit() > serviceConfiguration.getMaxSearchLimit()) {
            limit = serviceConfiguration.getMaxSearchLimit();
        }

        if (pagination != null && pagination.getOffset() != null) offset = pagination.getOffset();

        StringBuilder orderQuery = new StringBuilder();
        if (pagination != null) {
        	addOrderByClause(orderQuery, pagination);
        }
        finalQuery = finalQuery.replace("{order_by}", orderQuery.toString());

        if (limit == -1) {
            finalQuery = finalQuery.replace("{pagination}", "");
        } else {
            finalQuery = finalQuery.replace("{pagination}", " offset ?  limit ?  ");
            preparedStmtList.add(offset);
            preparedStmtList.add(limit);
        }

        return finalQuery;
    }


    /**
     * Add order by class and sort order
     *
     * @param queryBuilder string query builder
     * @param pagination   pagination object
     */
    private void addOrderByClause(StringBuilder queryBuilder, Pagination pagination) {

        if (StringUtils.isEmpty(pagination.getSortBy())) {
            queryBuilder.append(" ORDER BY plant_user.lastmodifiedtime ");
        } else if (pagination.getSortBy() == SortBy.id) {
            queryBuilder.append(" ORDER BY plant_user.id ");
        } else if (pagination.getSortBy() == SortBy.plantCode) {
            queryBuilder.append(" ORDER BY plant_user.plantCode ");
        } else if (pagination.getSortBy() == SortBy.plantUserUuid) {
            queryBuilder.append(" ORDER BY plant_user.plantUserUuid ");
        } else if (pagination.getSortBy() == SortBy.createdTime) {
            queryBuilder.append(" ORDER BY plant_user.createdtime ");
        }

        if (pagination.getSortOrder() == Pagination.SortOrder.ASC) {
            queryBuilder.append(" ASC ");
        } else {
            queryBuilder.append(" DESC ");
        }
    }
}
