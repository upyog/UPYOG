package org.egov.egf.contract.model;

import java.io.Serializable;

import org.egov.infra.validation.SanitizeHtml;

public class Bank implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -8344176995296804471L;
    private Integer id;
    @SanitizeHtml
    private String code;
    @SanitizeHtml
    private String name;
    @SanitizeHtml
    private String description;
    private Boolean active;
    @SanitizeHtml
    private String type;
    private AuditDetails auditDetails;

    public Bank() {
    }

    public Bank(final Integer id, final String code, final String name, final String description, final Boolean active,
            final String type, final AuditDetails auditDetails) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.description = description;
        this.active = active;
        this.type = type;
        this.auditDetails = auditDetails;
    }

    public Integer getId() {
        return id;
    }

    public void setId(final Integer id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(final String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public Boolean isActive() {
        return active;
    }

    public void setActive(final Boolean active) {
        this.active = active;
    }

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public AuditDetails getAuditDetails() {
        return auditDetails;
    }

    public void setAuditDetails(final AuditDetails auditDetails) {
        this.auditDetails = auditDetails;
    }

}
