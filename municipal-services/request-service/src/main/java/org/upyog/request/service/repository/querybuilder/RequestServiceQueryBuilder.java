package org.upyog.request.service.repository.querybuilder;

import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.upyog.request.service.web.models.*;
import java.util.List;

@Component
public class RequestServiceQueryBuilder {

    private static final String BOOKING_DETAILS_SEARCH_QUERY = (
            "SELECT ursbd.booking_id, booking_no, tanker_type, tanker_quantity, water_quantity, description, delivery_date, delivery_time, extra_charge, vendor_id, vehicle_id, driver_id, "
                    + "ursbd.createdby, ursbd.lastModifiedby, ursbd.createdtime, ursbd.lastmodifiedtime, "
                    + "appl.name, mobile_number, gender, email_id, alternate_number, "
                    + "addr.house_no, address_line_1, address_line_2, street_name, landmark, city, city_code, locality, locality_code, pincode "
                    + "FROM public.upyog_rs_tanker_booking_details ursbd "
                    + "join public.upyog_rs_applicant_details appl on ursbd.booking_id = appl.booking_id "
                    + "join public.upyog_rs_address_details addr on appl.applicant_id = addr.applicant_id ");


    public String getWaterTankerQuery(WaterTankerBookingSearchCriteria criteria, List<Object> preparedStmtList) {

        StringBuilder query = new StringBuilder(BOOKING_DETAILS_SEARCH_QUERY);

/* will change it when develop a method to get count details of Booking Details

        if (!criteria.isCountCall()) {
            query = new StringBuilder(BOOKING_DETAILS_SEARCH_QUERY);
        } else {
            query = new StringBuilder(applicationsCount);
    }*/

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
        return query.toString();
    }
    private void addClauseIfRequired(StringBuilder query, List<Object> preparedStmtList) {
        if (preparedStmtList.isEmpty()) {
            query.append(" WHERE ");
        } else {
            query.append(" AND ");
        }
    }
    }
