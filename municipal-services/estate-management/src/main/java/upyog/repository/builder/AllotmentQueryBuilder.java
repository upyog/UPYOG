package upyog.repository.builder;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import upyog.web.models.AllotmentSearchCriteria;

import java.util.ArrayList;
import java.util.List;

@Component
public class AllotmentQueryBuilder {
    
    private static final String BASE_SEARCH_QUERY = "SELECT " +
            "allotment_id, estate_no, tenant_id, user_uuid, allotee_name, mobile_number, " +
            "alternate_contact_no, email_id, agreement_start_date, agreement_end_date, " +
            "duration, rate, monthly_rent, advance_payment, allotment_date, " +
            "advance_payment_date, eoffice_file_no, asset_reference_no, property_type, " +
            "citizen_request_letter, allotment_letter, signed_deed, billing_cycle, " +
            "createdby, lastmodifiedby, createdtime, lastmodifiedtime " +
            "FROM ug_em_allotment_details";
    
    /**
     * Builds the search query based on the provided search criteria
     * 
     * @param criteria The search criteria for filtering allotments
     * @param preparedStmtList List to store prepared statement parameters
     * @return The constructed SQL query
     */
    public String getAllotmentSearchQuery(AllotmentSearchCriteria criteria, List<Object> preparedStmtList) {
        StringBuilder query = new StringBuilder(BASE_SEARCH_QUERY);
        List<String> conditions = new ArrayList<>();
        
        if (criteria != null) {
            // Add tenant id condition (mandatory)
            if (StringUtils.hasText(criteria.getTenantId())) {
                conditions.add("tenant_id = ?");
                preparedStmtList.add(criteria.getTenantId());
            }
            
            // Add asset/estate number condition
            if (StringUtils.hasText(criteria.getAssetNo())) {
                conditions.add("estate_no = ?");
                preparedStmtList.add(criteria.getAssetNo());
            }
            
            // Add allottee name condition
            if (StringUtils.hasText(criteria.getAlloteeName())) {
                conditions.add("allotee_name ILIKE ?");
                preparedStmtList.add("%" + criteria.getAlloteeName() + "%");
            }
            
            // Add user UUID condition
            if (StringUtils.hasText(criteria.getUserUuid())) {
                conditions.add("user_uuid = ?");
                preparedStmtList.add(criteria.getUserUuid());
            }
            
            // Add status condition (if needed for future use)
            if (StringUtils.hasText(criteria.getStatus())) {
                conditions.add("status = ?");
                preparedStmtList.add(criteria.getStatus());
            }
        }
        
        // Append conditions to query
        if (!conditions.isEmpty()) {
            query.append(" WHERE ");
            query.append(String.join(" AND ", conditions));
        }
        
        // Add order by clause
        query.append(" ORDER BY createdtime DESC");
        
        return query.toString();
    }
    
    /**
     * Builds query to search allotment by allotment ID
     * 
     * @param allotmentId The allotment ID (UUID)
     * @param tenantId The tenant ID
     * @param preparedStmtList List to store prepared statement parameters
     * @return The constructed SQL query
     */
    public String getAllotmentByIdQuery(String allotmentId, String tenantId, List<Object> preparedStmtList) {
        StringBuilder query = new StringBuilder(BASE_SEARCH_QUERY);
        query.append(" WHERE allotment_id = ? AND tenant_id = ?");
        preparedStmtList.add(allotmentId);
        preparedStmtList.add(tenantId);
        return query.toString();
    }
    
    /**
     * Builds query to search allotments by estate number
     * 
     * @param estateNo The estate number
     * @param preparedStmtList List to store prepared statement parameters
     * @return The constructed SQL query
     */
    public String getAllotmentsByEstateNoQuery(String estateNo, List<Object> preparedStmtList) {
        StringBuilder query = new StringBuilder(BASE_SEARCH_QUERY);
        query.append(" WHERE estate_no = ?");
        preparedStmtList.add(estateNo);
        return query.toString();
    }
}
