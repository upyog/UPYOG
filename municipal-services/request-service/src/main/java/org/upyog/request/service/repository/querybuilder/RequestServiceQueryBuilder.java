package org.upyog.request.service.repository.querybuilder;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.upyog.request.service.config.RequestServiceConfiguration;
import org.upyog.request.service.web.models.*;
import java.util.List;

@Slf4j
@Component
public class RequestServiceQueryBuilder {

    @Autowired
    private RequestServiceConfiguration requestServiceConfiguration;

    private static final String BOOKING_DETAILS_SEARCH_QUERY = (
            "SELECT ursbd.booking_id, booking_no, tanker_type, tanker_quantity, water_quantity, description, delivery_date, delivery_time, extra_charge, vendor_id, vehicle_id, driver_id, "
                    + "ursbd.createdby, ursbd.lastModifiedby, ursbd.createdtime, ursbd.lastmodifiedtime, "
                    + "appl.name, mobile_number, gender, email_id, alternate_number, "
                    + "addr.house_no, address_line_1, address_line_2, street_name, landmark, city, city_code, locality, locality_code, pincode "
                    + "FROM public.upyog_rs_tanker_booking_details ursbd "
                    + "join public.upyog_rs_applicant_details appl on ursbd.booking_id = appl.booking_id "
                    + "join public.upyog_rs_address_details addr on appl.applicant_id = addr.applicant_id ");

    private final String paginationWrapper = "SELECT * FROM " + "(SELECT *, ROW_NUMBER() OVER (ORDER BY createdtime DESC) AS offset_ FROM " + "({})"
            + " result) result_offset " + "WHERE offset_ > ? AND offset_ <= ?";

    private static final String applicationsCount = "SELECT count(ursbd.booking_id) "
            + " FROM upyog_rs_tanker_booking_details ursbd "
            + " join upyog_rs_applicant_details appl on ursbd.booking_id = appl.booking_id  \n";


    public String getWaterTankerQuery(WaterTankerBookingSearchCriteria criteria, List<Object> preparedStmtList) {

        StringBuilder query;

        if (!criteria.isCountCall()) {
            query = new StringBuilder(BOOKING_DETAILS_SEARCH_QUERY);
        } else {
            query = new StringBuilder(applicationsCount);
        }

        if (!ObjectUtils.isEmpty(criteria.getTenantId())) {
            addClauseIfRequired(query, preparedStmtList);
            query.append(" ursbd.tenant_id LIKE ? ");
            preparedStmtList.add("%" + criteria.getTenantId() + "%");
        }
        if (!ObjectUtils.isEmpty(criteria.getBookingNo())) {
            addClauseIfRequired(query, preparedStmtList);
            query.append(" ursbd.booking_no = ? ");
            preparedStmtList.add(criteria.getBookingNo());
        }
        if (!ObjectUtils.isEmpty(criteria.getMobileNumber())) {
            addClauseIfRequired(query, preparedStmtList);
            query.append(" appl.mobile_number = ? ");
            preparedStmtList.add(criteria.getMobileNumber());
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
                                        WaterTankerBookingSearchCriteria criteria) {

        int limit = requestServiceConfiguration.getDefaultLimit();
        int offset = requestServiceConfiguration.getDefaultOffset();
        String finalQuery = paginationWrapper.replace("{}", query);

        if (criteria.getLimit() == null && criteria.getOffset() == null) {
            limit = requestServiceConfiguration.getMaxSearchLimit();
        }

        if (criteria.getLimit() != null && criteria.getLimit() <= requestServiceConfiguration.getMaxSearchLimit()) {
            limit = criteria.getLimit();
        }

        if (criteria.getLimit() != null && criteria.getLimit() > requestServiceConfiguration.getMaxSearchLimit()) {
            limit = requestServiceConfiguration.getMaxSearchLimit();
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
