package org.upyog.tp.repository.querybuilder;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.upyog.tp.config.TreePruningConfiguration;
import org.upyog.tp.web.models.treePruning.TreePruningBookingSearchCriteria;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class TreePruningQueryBuilder {

    @Autowired
    private TreePruningConfiguration treePruningConfiguration;

    private static final String TREE_PRUNING_BOOKING_DETAILS_SEARCH_QUERY_WITH_PROFILE = (
            "SELECT uptbd.booking_id, booking_no, applicant_uuid, mobile_number, locality_code, reason_for_pruning, " +
                    "latitude, longitude, payment_date, application_date, payment_receipt_filestore_id, " +
                    "address_detail_id, booking_status, uptbd.createdby, uptbd.lastmodifiedby, uptbd.createdtime, " +
                    "uptbd.lastmodifiedtime, uptbd.tenant_id, " +
                    "doc.document_detail_id, doc.document_type, doc.filestore_id " +
                    "FROM public.upyog_rs_tree_pruning_booking_detail uptbd " +
                    "LEFT JOIN public.upyog_rs_tree_pruning_document_detail doc ON uptbd.booking_id = doc.booking_id"
    );
    
    private static final String TREE_PRUNING_BOOKING_DETAILS_SEARCH_QUERY = (
            "SELECT uptbd.booking_id, uptbd.booking_no, uptbd.applicant_uuid, uptbd.mobile_number, uptbd.locality_code, uptbd.reason_for_pruning, " +
                    "uptbd.latitude, uptbd.longitude, uptbd.payment_date, uptbd.application_date, uptbd.payment_receipt_filestore_id, " +
                    "uptbd.address_detail_id, uptbd.booking_status, uptbd.createdby, uptbd.lastmodifiedby, uptbd.createdtime, " +
                    "uptbd.lastmodifiedtime, uptbd.tenant_id, " +
                    "doc.document_detail_id, doc.document_type, doc.filestore_id, " +
                    "urad.applicant_id, urad.name, urad.mobile_number as applicant_mobile, urad.email_id, urad.alternate_number, " +
                    "uraddr.address_id, uraddr.house_no, uraddr.address_line_1, uraddr.address_line_2, uraddr.street_name, " +
                    "uraddr.landmark, uraddr.city, uraddr.city_code, uraddr.locality, uraddr.locality_code as addr_locality_code, uraddr.pincode " +
                    "FROM public.upyog_rs_tree_pruning_booking_detail uptbd " +
                    "LEFT JOIN public.upyog_rs_tree_pruning_document_detail doc ON uptbd.booking_id = doc.booking_id " +
                    "INNER JOIN public.upyog_rs_tree_pruning_applicant_details urad ON uptbd.booking_id = urad.booking_id " +
                    "INNER JOIN public.upyog_rs_tree_pruning_address_details uraddr ON urad.applicant_id = uraddr.applicant_id"
    );

    private final String paginationWrapper =
            "SELECT * FROM (SELECT *, ROW_NUMBER() OVER (ORDER BY createdtime DESC) AS offset_ FROM ({}) result) result_offset " +
                    "WHERE offset_ > ? AND offset_ <= ?";

    private static final String treePruningBookingCount =
            "SELECT count(uptbd.booking_id) FROM upyog_rs_tree_pruning_booking_detail uptbd";


    public String getTreePruningQuery(TreePruningBookingSearchCriteria criteria, List<Object> preparedStmtList) {

        StringBuilder query;

        if (!criteria.isCountCall()) {
            // Use different query based on isProfileEnabled
            if (treePruningConfiguration.getIsUserProfileEnabled()) {
                query = new StringBuilder(TREE_PRUNING_BOOKING_DETAILS_SEARCH_QUERY_WITH_PROFILE);
            } else {
                query = new StringBuilder(TREE_PRUNING_BOOKING_DETAILS_SEARCH_QUERY);
            }
        } else {
            query = new StringBuilder(treePruningBookingCount);
        }

        if (!ObjectUtils.isEmpty(criteria.getTenantId())) {
            addClauseIfRequired(query, preparedStmtList);
            query.append(" uptbd.tenant_id LIKE ? ");
            preparedStmtList.add("%" + criteria.getTenantId() + "%");
        }
        if (treePruningConfiguration.getIsUserProfileEnabled()) {
            addClauseIfRequired(query, preparedStmtList);
            query.append(" uptbd.applicant_uuid IS NOT NULL ");
        } else {
            // If user profile is not enabled, we don't need to filter by applicant UUID
            addClauseIfRequired(query, preparedStmtList);
            query.append(" uptbd.applicant_uuid IS NULL ");
        }
        if (!ObjectUtils.isEmpty(criteria.getBookingNo())) {
            addClauseIfRequired(query, preparedStmtList);
            
            // Create a comma-separated string of placeholders
            String bookingNosPlaceholders = String.join(",", Collections.nCopies(criteria.getBookingNo().split(",").length, "?"));
            
            query.append(" uptbd.booking_no IN (").append(bookingNosPlaceholders).append(")");

            // Add the booking numbers to the preparedStmtList
            String[] bookingNumbers = criteria.getBookingNo().split(",");
            Collections.addAll(preparedStmtList, bookingNumbers);
        }

        if (!ObjectUtils.isEmpty(criteria.getMobileNumber())) {
            addClauseIfRequired(query, preparedStmtList);
            query.append(" uptbd.mobile_number = ? ");
            preparedStmtList.add(criteria.getMobileNumber());
        }

        if(!ObjectUtils.isEmpty(criteria.getLocalityCode())){
            addClauseIfRequired(query, preparedStmtList);
            query.append(" uptbd.locality_code = ? ");
            preparedStmtList.add(criteria.getLocalityCode());
        }

        if (!ObjectUtils.isEmpty(criteria.getStatus())) {
            addClauseIfRequired(query, preparedStmtList);
            query.append(" uptbd.booking_status = ? ");
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
                                        TreePruningBookingSearchCriteria criteria) {

        int limit = treePruningConfiguration.getDefaultLimit();
        int offset = treePruningConfiguration.getDefaultOffset();
        String finalQuery = paginationWrapper.replace("{}", query);

        if (criteria.getLimit() == null && criteria.getOffset() == null) {
            limit = treePruningConfiguration.getMaxSearchLimit();
        }

        if (criteria.getLimit() != null && criteria.getLimit() <= treePruningConfiguration.getMaxSearchLimit()) {
            limit = criteria.getLimit();
        }

        if (criteria.getLimit() != null && criteria.getLimit() > treePruningConfiguration.getMaxSearchLimit()) {
            limit = treePruningConfiguration.getMaxSearchLimit();
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
