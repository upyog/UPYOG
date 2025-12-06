package org.egov.pqm.util;

import lombok.experimental.UtilityClass;

import java.util.Collections;
import java.util.List;

@UtilityClass
public class QueryBuilderUtil {

    public static Object addParamsToQuery(List<String> ids) {
        return String.join(",", Collections.nCopies(ids.size(), " ?"));
    }

    public static void addToWhereClause(List<Object> values, StringBuilder queryString) {
        if (values.isEmpty())
            queryString.append(" WHERE ");
        else {
            queryString.append(" AND");
        }
    }

    public static void addToPreparedStatement(List<Object> preparedStmtList, List<String> ids) {
        preparedStmtList.addAll(ids);
    }
}
