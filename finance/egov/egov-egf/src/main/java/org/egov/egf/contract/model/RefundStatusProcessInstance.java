package org.egov.egf.contract.model;

public class RefundStatusProcessInstance {

	private String tenantId;

	private String businessService;

	private String businessId;

	private String action;

	private String moduleName;

	private String comment;

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(final String tenantId) {
		this.tenantId = tenantId;
	}

	public String getBusinessService() {
		return businessService;
	}

	public void setBusinessService(final String businessService) {
		this.businessService = businessService;
	}

	public String getBusinessId() {
		return businessId;
	}

	public void setBusinessId(final String businessId) {
		this.businessId = businessId;
	}

	public String getAction() {
		return action;
	}

	public void setAction(final String action) {
		this.action = action;
	}

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(final String moduleName) {
		this.moduleName = moduleName;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(final String comment) {
		this.comment = comment;
	}
}