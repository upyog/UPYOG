package org.egov.infra.microservice.models;

import java.io.Serializable;

import org.egov.infra.validation.SanitizeHtml;

public class MasterDetail implements Serializable{
    
    @SanitizeHtml
    private String name;
    @SanitizeHtml
    private String filter;

    public MasterDetail(String name, String filter) {
        this.name = name;
        this.filter = filter;
    }
    public MasterDetail(){}
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getFilter() {
        return filter;
    }
    public void setFilter(String filter) {
        this.filter = filter;
    }
    

}
