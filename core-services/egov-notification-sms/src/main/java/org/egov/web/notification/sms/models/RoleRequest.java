package org.egov.web.notification.sms.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = { "code", "tenantId" })
public class RoleRequest {

	private String code;
	private String name;
	private String tenantId;

	public RoleRequest(Role domainRole) {
		this.code = domainRole.getCode();
		this.name = domainRole.getName();
		this.tenantId = domainRole.getTenantId();
	}

	public Role toDomain() {
		return Role.builder().code(code).name(name).tenantId(tenantId).build();
	}
}
