package org.egov.pg.repository;

import java.util.*;

import org.egov.pg.web.models.RefundCriteria;

public class RefundTransactionQueryBuilder {

    private static final String SEARCH_REFUND_SQL =
            "SELECT rf.id,rf.tenant_id, rf.refund_id, rf.original_txn_id, rf.service_code, " +
            "rf.original_amount, rf.refund_amount, rf.gateway_txn_id, rf.gateway, " +
            "rf.status, rf.additional_details, rf.created_by, rf.created_time, " +
            "rf.last_updated_by, rf.last_updated_time , rf.gateway_status_code, rf.gateway_status_msg " +
            "FROM eg_pg_transaction_refund rf ";

    private RefundTransactionQueryBuilder() {
    }

    static String getRefundSearchQueryByCreatedTimeRange(
            RefundCriteria refundCriteria,
            List<Object> preparedStmtList) {

        String query = buildQuery(refundCriteria,null,null, preparedStmtList);
        return query;
    }

    private static String buildQuery(
            RefundCriteria refundCriteria,
            Long startTime,
            Long endTime,
            List<Object> preparedStmtList) {

        StringBuilder builder = new StringBuilder(SEARCH_REFUND_SQL);
        List<String> conditions = new ArrayList<>();

        if (refundCriteria.getTenantId() != null) {
            conditions.add("rf.tenant_id = ?");
            preparedStmtList.add(refundCriteria.getTenantId());
        }

        if (refundCriteria.getRefundId() != null) {
            conditions.add("rf.refund_id = ?");
            preparedStmtList.add(refundCriteria.getRefundId());
        }

        if (refundCriteria.getOriginalTxnId() != null) {
            conditions.add("rf.original_txn_id = ?");
            preparedStmtList.add(refundCriteria.getOriginalTxnId());
        }

        // MULTI STATUS SUPPORT
        if (refundCriteria.getStatuses() != null && !refundCriteria.getStatuses().isEmpty()) {
            String placeholders = String.join(",",
                    java.util.Collections.nCopies(refundCriteria.getStatuses().size(), "?"));

            conditions.add("rf.status IN (" + placeholders + ")");

            refundCriteria.getStatuses()
                    .forEach(status -> preparedStmtList.add(status.toString()));
        }

        // Time range condition only if provided
        if (startTime != null && endTime != null) {
            conditions.add("rf.created_time >= ?");
            preparedStmtList.add(startTime);

            conditions.add("rf.created_time <= ?");
            preparedStmtList.add(endTime);
        }

        if (!conditions.isEmpty()) {
            builder.append(" WHERE ");
            builder.append(String.join(" AND ", conditions));
        }

        builder.append(" ORDER BY rf.created_time DESC");

        if (refundCriteria.getLimit() > 0) {
            builder.append(" LIMIT ?");
            preparedStmtList.add(refundCriteria.getLimit());
        }

        if (refundCriteria.getOffset() > 0) {
            builder.append(" OFFSET ?");
            preparedStmtList.add(refundCriteria.getOffset());
        }

        return builder.toString();
    }

  
    
    public static String getRefundSearchQueryByCreatedTimeRange(
            RefundCriteria refundCriteria,
            Long startTime,
            Long endTime,
            List<Object> preparedStmtList) {

        return buildQuery(refundCriteria, startTime, endTime, preparedStmtList);
    }
}

