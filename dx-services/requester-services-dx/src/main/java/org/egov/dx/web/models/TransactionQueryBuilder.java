package org.egov.dx.web.models;


import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.stereotype.Component;

@Component
public class TransactionQueryBuilder {
    private static final String SEARCH_TXN_SQL = "SELECT tlf.txn_id,tlf.tenant_Id, tlf.consumercode, tlf.filestore_id ,tlf.signed_filestore_id,  tlf.module, " +
    		"tlf.created_time, tlf.created_by, tlf.last_modified_by, tlf.last_modified_time " +
            "FROM eg_tl_esignedfilestore tlf ";

    private TransactionQueryBuilder() {
    }

    public String getTransactionSearchQueryByTxnId(TransactionCriteria transactionCriteria, List<Object> preparedStmtList) {
        String query = buildQuery(transactionCriteria, preparedStmtList);
        query = addOrderByClause(query);
        return query;
    }
    
    private String buildQuery(TransactionCriteria transactionCriteria, List<Object> preparedStmtList) {
        StringBuilder builder = new StringBuilder(TransactionQueryBuilder.SEARCH_TXN_SQL);
        Map<String, Object> queryParams = new HashMap<>();


        if (!Objects.isNull(transactionCriteria.getTxnId())) {
            queryParams.put("tlf.txn_id", transactionCriteria.getTxnId());
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

    private static String addOrderByClause(String query){
        return query + " order by tlf.created_time desc ";
    }

}