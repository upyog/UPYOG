package org.upyog.cdwm.repository.querybuilder;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.upyog.cdwm.config.CNDConfiguration;
import org.upyog.cdwm.web.models.CNDServiceSearchCriteria;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CNDServiceQueryBuilder {

    @Autowired
    private CNDConfiguration cndServiceConfiguration;

    // Base query for fetching application details
    private static final String APPLICATION_DETAILS_SEARCH_QUERY = 
        "SELECT ucad.application_id, application_number, application_type, vehicle_type, type_of_construction, deposit_centre_details, " +
        "applicant_detail_id, requested_pickup_date, application_status, additional_details, house_area, " +
        "construction_from_date, construction_to_date, property_type, total_waste_quantity, no_of_trips, vehicle_id, " +
        "vendor_id, pickup_date, completed_on, ucad.created_by, ucad.last_modified_by, ucad.created_time, " +
        "ucad.last_modified_time, ucad.tenant_id, ucad.applicant_detail_id, ucad.address_detail_id ,ucad.applicant_mobile_number FROM public.ug_cnd_application_details ucad ";
    
    // Pagination wrapper query
    private static final String PAGINATION_WRAPPER = 
        "SELECT * FROM (SELECT *, ROW_NUMBER() OVER (ORDER BY created_time DESC) AS offset_ FROM ({}) result) " +
        "result_offset WHERE offset_ > ? AND offset_ <= ?";
    
    // Query for counting applications
    private static final String APPLICATIONS_COUNT_QUERY = 
        "SELECT count(ucad.application_id) FROM ug_cnd_application_details ucad";
    
    /**
     * Builds query to fetch CND applications based on search criteria
     * @param criteria Search criteria containing filters
     * @param preparedStmtList List to store query parameters
     * @return Constructed SQL query
     */
    public String getCNDApplicationQuery(CNDServiceSearchCriteria criteria, List<Object> preparedStmtList) {
        
        StringBuilder query = new StringBuilder(criteria.isCountCall() ? APPLICATIONS_COUNT_QUERY : APPLICATION_DETAILS_SEARCH_QUERY);
        
        if (!ObjectUtils.isEmpty(criteria.getTenantId())) {
            addClauseIfRequired(query, preparedStmtList);
            query.append(" ucad.tenant_id LIKE ? ");
            preparedStmtList.add("%" + criteria.getTenantId() + "%");
        }
        
        if (!ObjectUtils.isEmpty(criteria.getApplicationNumber())) {
            addClauseIfRequired(query, preparedStmtList);
            
            // Creating placeholders for multiple application numbers
            String[] applicationNumbers = criteria.getApplicationNumber().split(",");
            String placeholders = String.join(",", Collections.nCopies(applicationNumbers.length, "?"));
            query.append(" ucad.application_number IN (").append(placeholders).append(")");
            
            Collections.addAll(preparedStmtList, applicationNumbers);
        }
        
        if (!ObjectUtils.isEmpty(criteria.getStatus())) {
            addClauseIfRequired(query, preparedStmtList);
            query.append(" ucad.application_status = ? ");
            preparedStmtList.add(criteria.getStatus());
        }

        if (!ObjectUtils.isEmpty(criteria.getMobileNumber())) {
            addClauseIfRequired(query, preparedStmtList);
            query.append(" ucad.applicant_mobile_number = ? ");
            preparedStmtList.add(criteria.getMobileNumber());
        }
        // If count query, return directly
        if (criteria.isCountCall()) {
            return query.toString();
        }
        
        // Apply pagination for non-count queries
        return addPaginationWrapper(query.toString(), preparedStmtList, criteria);
    }

    /**
     * Adds WHERE or AND clause based on existing query conditions
     * @param query Query string being built
     * @param preparedStmtList List of parameters for prepared statement
     */
    private void addClauseIfRequired(StringBuilder query, List<Object> preparedStmtList) {
        query.append(preparedStmtList.isEmpty() ? " WHERE " : " AND ");
    }

    /**
     * Wraps the given query with pagination logic
     * @param query Base query string
     * @param preparedStmtList List of parameters
     * @param criteria Search criteria containing limit and offset
     * @return Query with pagination applied
     */
    private String addPaginationWrapper(String query, List<Object> preparedStmtList, CNDServiceSearchCriteria criteria) {
        
        int limit = criteria.getLimit() != null ? Math.min(criteria.getLimit(), cndServiceConfiguration.getMaxSearchLimit()) : cndServiceConfiguration.getMaxSearchLimit();
        int offset = criteria.getOffset() != null ? criteria.getOffset() : cndServiceConfiguration.getDefaultOffset();
        
        String finalQuery = PAGINATION_WRAPPER.replace("{}", query);
        
        if (limit == -1) {
            return finalQuery.replace("WHERE offset_ > ? AND offset_ <= ?", "");
        }
        
        preparedStmtList.add(offset);
        preparedStmtList.add(offset + limit);
        
        return finalQuery;
    }
}
