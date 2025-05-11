package org.egov.hrms.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@AllArgsConstructor
@Getter
@NoArgsConstructor
@Setter
@ToString
@Builder
public class EmployeeWithWard {
	 private String uuid;
	    private String employeeId;
	    private String userId;
	    private String tenantId;
	    private String wardId;

	    // Getters and Setters
	    public String getUuid() {
	        return uuid;
	    }
	    public void setUuid(String uuid) {
	        this.uuid = uuid;
	    }

	    public String getEmployeeId() {
	        return employeeId;
	    }
	    public void setEmployeeId(String employeeId) {
	        this.employeeId = employeeId;
	    }

	    public String getUserId() {
	        return userId;
	    }
	    public void setUserId(String userId) {
	        this.userId = userId;
	    }

	    public String getTenantId() {
	        return tenantId;
	    }
	    public void setTenantId(String tenantId) {
	        this.tenantId = tenantId;
	    }

	    public String getWardId() {
	        return wardId;
	    }
	    public void setWardId(String wardId) {
	        this.wardId = wardId;
	    }

}
