package org.egov.pg.repository;

import java.util.*;

import org.egov.pg.web.models.RefundCriteria;

public class RefundTransactionQueryBuilder {

    private static final String SEARCH_REFUND_SQL =
            "SELECT rf.id,rf.tenant_id, rf.refund_id, rf.original_txn_id, rf.service_code, " +
            "rf.original_amount, rf.refund_amount, rf.gateway_txn_id, rf.gateway, " +
            "rf.status, rf.additional_details, rf.created_by, rf.created_time, " +
            "rf.last_updated_by, rf.last_updated_time, rf.tenant_id " +
            "FROM eg_pg_transaction_refund rf ";

    private RefundTransactionQueryBuilder() {
    }

    static String getRefundSearchQueryByCreatedTimeRange(
            RefundCriteria refundCriteria,
            List<Object> preparedStmtList) {

        String query = buildQuery(refundCriteria, preparedStmtList);
        query = addOrderByClause(query);
        query = addPagination(query, refundCriteria, preparedStmtList);
        return query;
    }

    private static String buildQuery(
            RefundCriteria refundCriteria,
            List<Object> preparedStmtList) {

        StringBuilder builder = new StringBuilder(SEARCH_REFUND_SQL);
        Map<String, Object> queryParams = new HashMap<>();

        if (!Objects.isNull(refundCriteria.getTenantId())) {
            queryParams.put("rf.tenant_id", refundCriteria.getTenantId());
        }
        
        if (!Objects.isNull(refundCriteria.getTenantId())) {
            queryParams.put("rf.original_txn_id", refundCriteria.getOriginalTxnId());
        }

        if (!Objects.isNull(refundCriteria.getRefundId())) {
            queryParams.put("rf.refund_id", refundCriteria.getRefundId());
        }

        if (!Objects.isNull(refundCriteria.getStatus())) {
            queryParams.put("rf.status", refundCriteria.getStatus().toString());
        }

        if (!queryParams.isEmpty()) {

            builder.append(" WHERE ");

            Iterator<Map.Entry<String, Object>> iterator = queryParams.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, Object> entry = iterator.next();
                builder.append(entry.getKey()).append(" = ? ");

                preparedStmtList.add(entry.getValue());

                if (iterator.hasNext())
                    builder.append(" AND ");
            }
        }

        return builder.toString();
    }

    private static String addPagination(
            String query,
            RefundCriteria refundCriteria,
            List<Object> preparedStmtList) {

        if (refundCriteria.getLimit() > 0) {
            query = query + " limit ? ";
            preparedStmtList.add(refundCriteria.getLimit());
        }

        if (refundCriteria.getOffset() > 0) {
            query = query + " offset ? ";
            preparedStmtList.add(refundCriteria.getOffset());
        }

        return query;
    }

    private static String addOrderByClause(String query) {
        return query + " order by rf.created_time desc ";
    }
}

