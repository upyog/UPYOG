package org.egov.pgr.utils;

import java.util.List;

public class ReportQuery {

    private String query;
    private List<Object> params;

    public ReportQuery(String query, List<Object> params) {
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