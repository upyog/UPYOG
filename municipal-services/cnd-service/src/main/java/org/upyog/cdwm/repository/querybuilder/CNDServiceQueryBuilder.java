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

    private static final String APPLICATION_DETAILS_SEARCH_QUERY = (
            "SELECT ucad.application_id, application_number, application_type, type_of_construction, deposit_centre_details, applicant_detail_id, requested_pickup_date, application_status, additional_details, house_area, construction_from_date, construction_to_date, property_type, total_waste_quantity, no_of_trips, vehicle_id, vendor_id, pickup_date, completed_on, "
                    + "ucad.created_by, ucad.last_modified_by, ucad.created_time, ucad.last_modified_time, ucad.tenant_id "
                    + "FROM public.ug_cnd_application_details ucad ");
    
    private final String paginationWrapper = "SELECT * FROM " + "(SELECT *, ROW_NUMBER() OVER (ORDER BY created_time DESC) AS offset_ FROM " + "({})"
            + " result) result_offset " + "WHERE offset_ > ? AND offset_ <= ?";

    private static final String applicationsCount = "SELECT count(ucad.application_id) "
            + " FROM ug_cnd_application_details ucad";


    public String getCNDApplicationQuery(CNDServiceSearchCriteria criteria, List<Object> preparedStmtList) {

        StringBuilder query;

        if (!criteria.isCountCall()) {
            query = new StringBuilder(APPLICATION_DETAILS_SEARCH_QUERY);
        } else {
            query = new StringBuilder(applicationsCount);
        }

        if (!ObjectUtils.isEmpty(criteria.getTenantId())) {
            addClauseIfRequired(query, preparedStmtList);
            query.append(" ucad.tenant_id LIKE ? ");
            preparedStmtList.add("%" + criteria.getTenantId() + "%");
        }
        if (!ObjectUtils.isEmpty(criteria.getApplicationNumber())) {
            addClauseIfRequired(query, preparedStmtList);
            
            // Create a comma-separated string of placeholders
            String bookingNosPlaceholders = String.join(",", Collections.nCopies(criteria.getApplicationNumber().split(",").length, "?"));
            
            query.append(" ucad.application_number IN (").append(bookingNosPlaceholders).append(")");

            // Add the booking numbers to the preparedStmtList
            String[] bookingNumbers = criteria.getApplicationNumber().split(",");
            Collections.addAll(preparedStmtList, bookingNumbers);
        }


        if (!ObjectUtils.isEmpty(criteria.getStatus())) {
            addClauseIfRequired(query, preparedStmtList);
            query.append(" ucad.application_status = ? ");
            preparedStmtList.add(criteria.getStatus());
        }

        // Return count query directly without applying pagination
        if (criteria.isCountCall()) {
        	  return query.toString();
          
        }
      
        // Apply pagination for non-count queries
        return addPaginationWrapper(query.toString(), preparedStmtList, criteria);

    }
    private void addClauseIfRequired(StringBuilder query, List<Object> preparedStmtList) {
        if (preparedStmtList.isEmpty()) {
            query.append(" WHERE ");
        } else {
            query.append(" AND ");
        }
    }

    private String addPaginationWrapper(String query, List<Object> preparedStmtList,
                                        CNDServiceSearchCriteria criteria) {

        int limit = cndServiceConfiguration.getDefaultLimit();
        int offset = cndServiceConfiguration.getDefaultOffset();
        String finalQuery = paginationWrapper.replace("{}", query);

        if (criteria.getLimit() == null && criteria.getOffset() == null) {
            limit = cndServiceConfiguration.getMaxSearchLimit();
        }

        if (criteria.getLimit() != null && criteria.getLimit() <= cndServiceConfiguration.getMaxSearchLimit()) {
            limit = criteria.getLimit();
        }

        if (criteria.getLimit() != null && criteria.getLimit() > cndServiceConfiguration.getMaxSearchLimit()) {
            limit = cndServiceConfiguration.getMaxSearchLimit();
        }

        if (criteria.getOffset() != null)
            offset = criteria.getOffset();

        if (limit == -1) {
            finalQuery = finalQuery.replace("WHERE offset_ > ? AND offset_ <= ?", "");
        } else {
            preparedStmtList.add(offset);
            preparedStmtList.add(offset+limit);
        }

        return finalQuery;

    }

    }
