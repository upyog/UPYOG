package org.upyog.rs.repository.querybuilder;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.upyog.rs.config.RequestServiceConfiguration;
import org.upyog.rs.web.models.mobileToilet.MobileToiletBookingSearchCriteria;
import org.upyog.rs.web.models.waterTanker.WaterTankerBookingSearchCriteria;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class RequestServiceQueryBuilder {

    @Autowired
    private RequestServiceConfiguration requestServiceConfiguration;

    private static final String WATER_TANKER_BOOKING_DETAILS_SEARCH_QUERY = (
            "SELECT ursbd.booking_id, booking_no,applicant_uuid, mobile_number, locality_code, tanker_type, tanker_quantity, water_quantity, description, " +
                    "delivery_date, delivery_time, extra_charge, vendor_id, vehicle_id, driver_id, vehicle_type, " +
                    "vehicle_capacity,address_detail_id, booking_status, ursbd.createdby, ursbd.lastModifiedby, ursbd.createdtime, " +
                    "ursbd.lastmodifiedtime, ursbd.tenant_id " +
                    "FROM public.upyog_rs_tanker_booking_details ursbd"
    );

    private static final String MOBILE_TOILET_BOOKING_DETAILS_SEARCH_QUERY = (
            "SELECT urmt.booking_id, booking_no, applicant_uuid, no_of_mobile_toilet, mobile_number, locality_code, " +
                    "description, delivery_from_date, delivery_to_date, delivery_from_time, delivery_to_time, vendor_id, " +
                    "vehicle_id, driver_id, vehicle_type, vehicle_capacity,address_detail_id, booking_status, urmt.createdby, " +
                    "urmt.lastModifiedby, urmt.createdtime, urmt.lastmodifiedtime, urmt.tenant_id " +
                    "FROM public.upyog_rs_mobile_toilet_booking_details urmt"
    );

    private final String paginationWrapper =
            "SELECT * FROM (SELECT *, ROW_NUMBER() OVER (ORDER BY createdtime DESC) AS offset_ FROM ({}) result) result_offset " +
                    "WHERE offset_ > ? AND offset_ <= ?";

    private static final String waterTankerBookingCount =
            "SELECT count(ursbd.booking_id) FROM upyog_rs_tanker_booking_details ursbd";

    private static final String mobileToiletBookingCount =
            "SELECT count(urmt.booking_id) FROM upyog_rs_mobile_toilet_booking_details urmt";


    public String getWaterTankerQuery(WaterTankerBookingSearchCriteria criteria, List<Object> preparedStmtList) {

        StringBuilder query;

        if (!criteria.isCountCall()) {
            query = new StringBuilder(WATER_TANKER_BOOKING_DETAILS_SEARCH_QUERY);
        } else {
            query = new StringBuilder(waterTankerBookingCount);
        }

        if (!ObjectUtils.isEmpty(criteria.getTenantId())) {
            addClauseIfRequired(query, preparedStmtList);
            query.append(" ursbd.tenant_id LIKE ? ");
            preparedStmtList.add("%" + criteria.getTenantId() + "%");
        }
        if (!ObjectUtils.isEmpty(criteria.getBookingNo())) {
            addClauseIfRequired(query, preparedStmtList);
            
            // Create a comma-separated string of placeholders
            String bookingNosPlaceholders = String.join(",", Collections.nCopies(criteria.getBookingNo().split(",").length, "?"));
            
            query.append(" ursbd.booking_no IN (").append(bookingNosPlaceholders).append(")");

            // Add the booking numbers to the preparedStmtList
            String[] bookingNumbers = criteria.getBookingNo().split(",");
            Collections.addAll(preparedStmtList, bookingNumbers);
        }

        if (!ObjectUtils.isEmpty(criteria.getMobileNumber())) {
            addClauseIfRequired(query, preparedStmtList);
            query.append(" ursbd.mobile_number = ? ");
            preparedStmtList.add(criteria.getMobileNumber());
        }

        if(!ObjectUtils.isEmpty(criteria.getLocalityCode())){
            addClauseIfRequired(query, preparedStmtList);
            query.append(" ursbd.locality_code = ? ");
            preparedStmtList.add(criteria.getLocalityCode());
        }

        if (!ObjectUtils.isEmpty(criteria.getStatus())) {
            addClauseIfRequired(query, preparedStmtList);
            query.append(" ursbd.booking_status = ? ");
            preparedStmtList.add(criteria.getStatus());
        }

        // Return count query directly without applying pagination
        if (criteria.isCountCall()) {
            return query.toString();
        }

        // Apply pagination for non-count queries
        return addPaginationWrapper(query.toString(), preparedStmtList, criteria);

    }

    public String getMobileToiletQuery(MobileToiletBookingSearchCriteria criteria, List<Object> preparedStmtList) {

        StringBuilder query;

        if (!criteria.isCountCall()) {
            query = new StringBuilder(MOBILE_TOILET_BOOKING_DETAILS_SEARCH_QUERY);
        } else {
            query = new StringBuilder(mobileToiletBookingCount);
        }

        if (!ObjectUtils.isEmpty(criteria.getTenantId())) {
            addClauseIfRequired(query, preparedStmtList);
            query.append(" urmt.tenant_id LIKE ? ");
            preparedStmtList.add("%" + criteria.getTenantId() + "%");
        }

        if (!ObjectUtils.isEmpty(criteria.getBookingNo())) {
            addClauseIfRequired(query, preparedStmtList);

            // Create a comma-separated string of placeholders
            String bookingNosPlaceholders = String.join(",",
                    Collections.nCopies(criteria.getBookingNo().split(",").length, "?")
            );

            query.append(" urmt.booking_no IN (").append(bookingNosPlaceholders).append(")");

            // Add the booking numbers to the preparedStmtList
            String[] bookingNumbers = criteria.getBookingNo().split(",");
            Collections.addAll(preparedStmtList, bookingNumbers);
        }

        if (!ObjectUtils.isEmpty(criteria.getMobileNumber())) {
            addClauseIfRequired(query, preparedStmtList);
            query.append(" urmt.mobile_number = ? ");
            preparedStmtList.add(criteria.getMobileNumber());
        }

        if(!ObjectUtils.isEmpty(criteria.getLocalityCode())){
            addClauseIfRequired(query, preparedStmtList);
            query.append(" urmt.locality_code = ? ");
            preparedStmtList.add(criteria.getLocalityCode());
        }

        if (!ObjectUtils.isEmpty(criteria.getStatus())) {
            addClauseIfRequired(query, preparedStmtList);
            query.append(" urmt.booking_status = ? ");
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

    private String addPaginationWrapper(String query, List<Object> preparedStmtList,
                                        MobileToiletBookingSearchCriteria criteria) {

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
