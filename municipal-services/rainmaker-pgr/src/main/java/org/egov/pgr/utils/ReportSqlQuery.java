package org.egov.pgr.utils;

import java.util.List;

public class ReportSqlQuery {

    private final String query;
    private final List<Object> params;

    public ReportSqlQuery(String query, List<Object> params) {
        this.query = query;
        this.params = params;
    }

    public String getQuery() {
        return query;
    }

    public List<Object> getParams() {
        return params;
    }
}