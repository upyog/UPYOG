
package org.egov.finance.inbox.model;

import org.egov.finance.inbox.customannotation.SafeHtml;

import jakarta.validation.constraints.NotNull;

public class HODDepartment {

    private Long id;

    @NotNull
    private Long department;
    @SafeHtml
    private String tenantId;

    public HODDepartment(Long id, Long department, String tenantId) {
        this.id = id;
        this.department = department;
        this.tenantId = tenantId;
    }

    public HODDepartment() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDepartment() {
        return department;
    }

    public void setDepartment(Long department) {
        this.department = department;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    };

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((department == null) ? 0 : department.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((tenantId == null) ? 0 : tenantId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        HODDepartment other = (HODDepartment) obj;
        if (department == null) {
            if (other.department != null)
                return false;
        } else if (!department.equals(other.department))
            return false;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (tenantId == null) {
            if (other.tenantId != null)
                return false;
        } else if (!tenantId.equals(other.tenantId))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "HODDepartment [id=" + id + ", department=" + department + ", tenantId=" + tenantId + "]";
    }

}