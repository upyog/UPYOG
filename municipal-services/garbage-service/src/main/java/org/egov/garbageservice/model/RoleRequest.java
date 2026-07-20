package org.egov.garbageservice.model;

import org.egov.tracer.annotations.CustomSafeHtml;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
/**
 * Minimal role lookup key (code and tenantId) for user service role APIs.
 * Used when assigning or validating roles during user provisioning.
 */
@EqualsAndHashCode(of = { "code", "tenantId" })
public class RoleRequest {

	@CustomSafeHtml
	private String code;
	@CustomSafeHtml
	private String name;
	@CustomSafeHtml
	private String tenantId;

	public RoleRequest(RoleV2 domainRole) {
		this.code = domainRole.getCode();
		this.name = domainRole.getName();
		this.tenantId = domainRole.getTenantId();
	}

	public RoleV2 toDomain() {
		return RoleV2.builder().code(code).name(name).tenantId(tenantId).build();
	}
}
