package org.egov.infra.microservice.models;

import org.egov.infra.validation.SanitizeHtml;

public class BusinessCategory {

    private Long id;
    @SanitizeHtml
    private String code;
    @SanitizeHtml
    private String name;

    private Boolean active;
    @SanitizeHtml
    private String tenantId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

}
