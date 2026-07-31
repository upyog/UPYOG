package upyog.repository.builder;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import upyog.web.models.AssetSearchCriteria;
import upyog.util.EstateUtil;

import java.util.ArrayList;
import java.util.List;

@Component
public class AssetQueryBuilder {
    
    // Base select query from the asset table getting all the fields for asset row mapper class
    private static final String BASE_SEARCH_QUERY = "SELECT " +
            "asset_id, estate_no, tenant_id, building_name, building_no, ref_asset_no, " +
            "floor, locality_code, locality, total_floor_area, dimension_length, dimension_width, " +
            "rent_rate, name, description, classification, parent_category, category, subcategory, " +
            "department, asset_type, asset_status, asset_allotment_type, asset_allotment_status, " +
            "additional_details, createdby, lastmodifiedby, createdtime, lastmodifiedtime " +
            "FROM ug_em_asset_details";
    
    /**
     * Builds the search query based on the provided search criteria
     * 
     * @param criteria The search criteria for filtering assets
     * @param preparedStmtList List to store prepared statement parameters
     * @return The constructed SQL query
     */
    public String getAssetSearchQuery(AssetSearchCriteria criteria, List<Object> preparedStmtList) {
        StringBuilder query = new StringBuilder(BASE_SEARCH_QUERY);
        List<String> conditions = new ArrayList<>();
        
        if (criteria != null) {
            if (StringUtils.hasText(criteria.getTenantId())) {
                conditions.add("tenant_id = ?");
                preparedStmtList.add(criteria.getTenantId());
            }
            
            if (StringUtils.hasText(criteria.getEstateNo())) {
                conditions.add("estate_no = ?");
                preparedStmtList.add(criteria.getEstateNo());
            }
            
            if (StringUtils.hasText(criteria.getRefAssetNo())) {
                conditions.add("ref_asset_no = ?");
                preparedStmtList.add(criteria.getRefAssetNo());
            }
            
            if (StringUtils.hasText(criteria.getBuildingName())) {
                conditions.add("building_name ILIKE ?");
                preparedStmtList.add("%" + criteria.getBuildingName() + "%");
            }
            
            if (StringUtils.hasText(criteria.getLocality())) {
                conditions.add("locality ILIKE ?");
                preparedStmtList.add("%" + criteria.getLocality() + "%");
            }
            
            if (StringUtils.hasText(criteria.getAssetStatus())) {
                conditions.add("asset_status = ?");
                preparedStmtList.add(criteria.getAssetStatus());
            }
        }
        
        if (!conditions.isEmpty()) {
            query.append(" WHERE ");
            query.append(String.join(" AND ", conditions));
        }
        
        // Add order by clause
        query.append(" ORDER BY createdtime DESC");
        
        return EstateUtil.addPaginationWrapper(query.toString(), preparedStmtList, criteria.getLimit(), criteria.getOffset());
    }
    
    /**
     * Builds query to search asset by reference asset number (from asset service)
     * 
     * @param refAssetNo The reference asset number
     * @param tenantId The tenant ID
     * @param preparedStmtList List to store prepared statement parameters
     * @return The constructed SQL query
     */
    public String getAssetByRefNoQuery(String refAssetNo, String tenantId, List<Object> preparedStmtList) {
        StringBuilder query = new StringBuilder(BASE_SEARCH_QUERY);
        query.append(" WHERE ref_asset_no = ? AND tenant_id = ?");
        preparedStmtList.add(refAssetNo);
        preparedStmtList.add(tenantId);
        query.append(" ORDER BY createdtime DESC");
        return query.toString();
    }
}
