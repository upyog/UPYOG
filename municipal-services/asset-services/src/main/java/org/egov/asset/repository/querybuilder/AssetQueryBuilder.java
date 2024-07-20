package org.egov.asset.repository.querybuilder;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.egov.asset.config.AssetConfiguration;
import org.egov.asset.web.models.AssetSearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

@Component
public class AssetQueryBuilder {
	
	@Autowired
    private AssetConfiguration config;

	private static final String LEFT_OUTER_JOIN_STRING = " LEFT OUTER JOIN ";

    private static final String QUERY = "SELECT * FROM eg_asset_assetdetails asset" 
    		+ LEFT_OUTER_JOIN_STRING + "eg_asset_addressdetails address on asset.id = address.asset_id"
    		+ LEFT_OUTER_JOIN_STRING + "eg_asset_document doc on asset.id = doc.assetid"
    		+ LEFT_OUTER_JOIN_STRING + "eg_asset_assignmentdetails assign on asset.id = assign.assetid ";
    		//+ " ORDER BY eg_asset_assetdetails.createdtime DESC";

    private final String paginationWrapper = "SELECT * FROM "
            + "(SELECT *, DENSE_RANK() OVER () offset_ FROM " + "({})"
            + " result) result_offset " + "WHERE offset_ > ? AND offset_ <= ?";
    
    //private final String countWrapper = "SELECT COUNT(DISTINCT(bpa_id)) FROM ({INTERNAL_QUERY}) as asset_count";
	
    /**
     * To give the Search query based on the requirements.
     * 
     * @param criteria ASSET search criteria
     * @param preparedStmtList values to be replaced on the query
     * @return Final Search Query
     */
	public String getAssetSearchQuery(AssetSearchCriteria criteria, List<Object> preparedStmtList) {
		StringBuilder builder = new StringBuilder(QUERY);
		//String query = "SELECT * FROM public.eg_asset_assetdetails ORDER BY createdtime DESC;";
		
		if (criteria.getTenantId() != null) {
            if (criteria.getTenantId().split("\\.").length == 1) {

                addClauseIfRequired(preparedStmtList, builder);
                builder.append(" asset.tenantid like ?");
                preparedStmtList.add('%' + criteria.getTenantId() + '%');
            } else {
                addClauseIfRequired(preparedStmtList, builder);
                builder.append(" asset.tenantid=? ");
                preparedStmtList.add(criteria.getTenantId());
            }
        }
		
		List<String> ids = criteria.getIds();
        if (!CollectionUtils.isEmpty(ids)) {
            addClauseIfRequired(preparedStmtList, builder);
            builder.append(" asset.id IN (").append(createQuery(ids)).append(")");
            addToPreparedStatement(preparedStmtList, ids);
        }
        
        String applicationNo = criteria.getApplicationNo();
        if (applicationNo != null) {
            List<String> applicationNos = Arrays.asList(applicationNo.split(","));
            addClauseIfRequired(preparedStmtList, builder);
            builder.append(" asset.applicationNo IN (").append(createQuery(applicationNos)).append(")");
            addToPreparedStatement(preparedStmtList, applicationNos);
        }
        
        // createdby search criteria 
        List<String> createdBy = criteria.getCreatedBy();
		if (!CollectionUtils.isEmpty(createdBy)) {
            addClauseIfRequired(preparedStmtList, builder);
            builder.append(" asset.createdby IN (").append(createQuery(createdBy)).append(")");
            addToPreparedStatement(preparedStmtList, createdBy);
        }
		
		// Status wise search criteria 
        String status = criteria.getStatus();
        if (status != null) {
            List<String> statusList = Arrays.asList(status.split(","));
            addClauseIfRequired(preparedStmtList, builder);
            builder.append(" asset.status IN (").append(createQuery(statusList)).append(")");
            addToPreparedStatement(preparedStmtList, statusList);
        }
        
        // ParentCategory wise search criteria 
        String assetParentCategoryList = criteria.getAssetParentCategory();
        if (assetParentCategoryList != null) {
            List<String>  assetParentCategory = Arrays.asList(assetParentCategoryList.split(","));
            addClauseIfRequired(preparedStmtList, builder);
            builder.append(" UPPER(asset.parentCategory) IN (").append(createQuery(assetParentCategory)).append(")");
            addToPreparedStatement(preparedStmtList, assetParentCategory);
        }
        
        // Classification wise search criteria 
        String classification = criteria.getAssetClassification();
        if (classification != null) {
            List<String>  classificationList = Arrays.asList(classification.split(","));
            addClauseIfRequired(preparedStmtList, builder);
            builder.append(" UPPER(asset.classification) IN (").append(createQuery(classificationList)).append(")");
            addToPreparedStatement(preparedStmtList, classificationList);
        }
		
        
		// Approval from approvaldate and to approvaldate search criteria 
		Long approvalDt = criteria.getApprovalDate();
        if (approvalDt != null) {

            Calendar approvalDate = Calendar.getInstance();
            approvalDate.setTimeInMillis(approvalDt);

            int year = approvalDate.get(Calendar.YEAR);
            int month = approvalDate.get(Calendar.MONTH);
            int day = approvalDate.get(Calendar.DATE);

            Calendar approvalStrDate = Calendar.getInstance();
            approvalStrDate.setTimeInMillis(0);
            approvalStrDate.set(year, month, day, 0, 0, 0);

            Calendar approvalEndDate = Calendar.getInstance();
            approvalEndDate.setTimeInMillis(0);
            approvalEndDate.set(year, month, day, 23, 59, 59);
            addClauseIfRequired(preparedStmtList, builder);
            builder.append(" asset.approvalDate BETWEEN ").append(approvalStrDate.getTimeInMillis()).append(" AND ")
                    .append(approvalEndDate.getTimeInMillis());
        }
        
        // Approval from createddate and to createddate search criteria 
        if (criteria.getFromDate() != null && criteria.getToDate() != null) {
            addClauseIfRequired(preparedStmtList, builder);
            builder.append(" asset.createdtime BETWEEN ").append(criteria.getFromDate()).append(" AND ")
                    .append(criteria.getToDate());
        } else if (criteria.getFromDate() != null && criteria.getToDate() == null) {
            addClauseIfRequired(preparedStmtList, builder);
            builder.append(" asset.createdtime >= ").append(criteria.getFromDate());
        }
        return addPaginationWrapper(builder.toString(), preparedStmtList, criteria);
	}
	
	/**
     * add if clause to the Statement if required or else AND
     * @param values
     * @param queryString
     */
    private void addClauseIfRequired(List<Object> values, StringBuilder queryString) {
        if (values.isEmpty())
            queryString.append(" WHERE ");
        else {
            queryString.append(" AND");
        }
    }
    
    /**
     * add values to the preparedStatment List
     * @param preparedStmtList
     * @param ids
     */
    private void addToPreparedStatement(List<Object> preparedStmtList, List<String> ids) {
        ids.forEach(id -> {
            preparedStmtList.add(id);
        });
    }
    
    /**
     * produce a query input for the multiple values
     * @param ids
     * @return
     */
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
    
    /**
     * 
     * @param query prepared Query
     * @param preparedStmtList values to be replased on the query
     * @param criteria bpa search criteria
     * @return the query by replacing the placeholders with preparedStmtList
     */
    private String addPaginationWrapper(String query, List<Object> preparedStmtList, AssetSearchCriteria criteria) {

        int limit = config.getDefaultLimit();
        int offset = config.getDefaultOffset();
        String finalQuery = paginationWrapper.replace("{}", query);

        if(criteria.getLimit() == null && criteria.getOffset() == null) {
        	limit = config.getMaxSearchLimit();
        } 
        
        if (criteria.getLimit() != null && criteria.getLimit() <= config.getMaxSearchLimit())
            limit = criteria.getLimit();

        if (criteria.getLimit() != null && criteria.getLimit() > config.getMaxSearchLimit()) {
            limit = config.getMaxSearchLimit();
        }

        if (criteria.getOffset() != null)
            offset = criteria.getOffset();

        if (limit == -1) {
            finalQuery = finalQuery.replace("WHERE offset_ > ? AND offset_ <= ?", "");
        } else {
            preparedStmtList.add(offset);
            preparedStmtList.add(limit + offset);
        }

        return finalQuery;

    }

}
