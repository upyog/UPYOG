package org.egov.rentlease.util;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.egov.common.contract.request.Role;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Component
public class RentConstants {
	
	@Value("${workflow.context.path}")
	public String workflowHost;
	
	@Value("${workflow.transition.path}")
	public String workflowEndpointTransition;
	
	@Value("${workflow.business.search}")
	public String workflowBusinessServiceSearchPath;
	
	@Value("${asset.context.path}")
	public String assetHost;
	
	@Value("${asset.transition.path}")
	public String assetEndPoint;

	public static final String RENT_LEASE_CREATION = "create-rent-lease";
	public static final String RENT_LEASE_INITIATED = "INITIATED";
	public static final String RENT_LEASE_CONSTANT = "RLA";
	public static final String RENT_LEASE_VERIFIER = "Rent Lease Verifier";
	public static final String RENT_LEASE_APPROVER ="Rent Lease Approver";
	public static final String STATUS_PENDINGFORVERIFICATION ="PENDINGFORVERIFICATION";
	public static final String STATUS_PENDINGFORAPPROVAL ="PENDINGFORAPPROVAL";
	public static final String STATE_LEVEL_TEENENT_ID = "hp";
	 public static final String RENT_STATUS_BOOKED = "BOOKED";
	

	public static List<String> getRolesByTenantId(String tenantId, List<Role> roles) {
		List<String> roleCodes = roles.stream()
				.filter(role -> StringUtils.equalsIgnoreCase(role.getTenantId(), tenantId)).map(role -> role.getCode())
				.collect(Collectors.toList());
		return roleCodes;
	}

}
