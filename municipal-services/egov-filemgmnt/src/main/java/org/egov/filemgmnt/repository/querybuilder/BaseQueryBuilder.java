package org.egov.filemgmnt.repository.querybuilder;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;

class BaseQueryBuilder {

    void addIdsFilter(String column, List<String> ids, StringBuilder query, List<Object> paramValues) {
        if (CollectionUtils.isNotEmpty(ids)) {
            addWhereClause(paramValues, query);
            query.append(column)
                 .append(" IN (")
                 .append(getStatementParameters(ids.size()))
                 .append(')');
            ids.forEach(paramValues::add);
        }
    }

    private void addWhereClause(List<Object> values, StringBuilder query) {
        if (CollectionUtils.isEmpty(values)) {
            query.append(" WHERE ");
        } else {
            query.append(" AND ");
        }
    }

    private String getStatementParameters(int count) {
        return Collections.nCopies(count, "LOWER(?)")
                          .stream()
                          .collect(Collectors.joining(", "));
    }
}
