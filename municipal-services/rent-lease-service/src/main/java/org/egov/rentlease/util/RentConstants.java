package org.egov.rentlease.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
	@Value("${egov.bill.context.host}")
	public String billHost;

	@Value("${egov.bill.endpoint.fetch}")
	public String fetchBillEndpoint;

	@Value("${egov.bill.endpoint.search}")
	public String searchBillEndpoint;
    
    @Value("${egov.demand.create.endpoint}")
    public String demandCreateEndpoint;
    
    @Value("${egov.demand.search.endpoint}")
    public String demandSearchEndpoint;
    
    @Value("${egov.demand.update.endpoint}")
    public String demandUpdateEndpoint;


	public static final String RENT_LEASE_CREATION = "create-rent-lease";
	public static final String RENT_LEASE_INITIATED = "INITIATED";
	
	public static final String RENT_LEASE_CONSTANT = "RLA";
	
	public static final String RENT_LEASE_VERIFIER = "Rent Lease Verifier";
	public static final String RENT_LEASE_APPROVER ="Rent Lease Approver";
	public static final String STATUS_PENDINGFORVERIFICATION ="PENDINGFORVERIFICATION";
	public static final String STATUS_PENDINGFORAPPROVAL ="PENDINGFORAPPROVAL";
	public static final String STATE_LEVEL_TEENENT_ID = "hp";
	public static final String RENT_STATUS_BOOKED = "BOOKED";
	public static final String ACTION_INITIATE = "INITIATE";
	
	public static final String WORKFLOW_SITE_BOOKING_MODULE = "RLA";
	
	public static final String ACTION_FORWARD_TO_VERIFIER = "FORWARD_TO_VERIFIER";

	public static final String ACTION_FORWARD_TO_APPROVER = "FORWARD_TO_APPROVER";

	public static final String ACTION_RETURN_TO_INITIATOR = "RETURN_TO_INITIATOR";

	public static final String ACTION_RETURN_TO_VERIFIER = "RETURN_TO_VERIFIER";

	public static final String ACTION_RETURN_TO_INITIATOR_FOR_PAYMENT = "RETURN_TO_INITIATOR_FOR_PAYMENT";

	public static final String STATUS_INITIATED = "INITIATED";

	public static final String STATUS_PENDINGFORMODIFICATION = "PENDINGFORMODIFICATION";
	
	public static final String STATUS_PENDINGFORPAYMENT = "PENDINGFORPAYMENT";

	public static final String STATUS_APPROVED = "APPROVED";
	
	public static final String STATUS_REVOKED ="REVOKED";
	
	public static final String STATUS_CANCEL ="CANCEL";
	
	public static final String RENT_LEASE_BOOKING_UPDATE_KAFKA_TOPIC = "update-rent-lease-booking";
	
    public static final String BILLING_TAX_HEAD_MASTER_CODE = "LCF.Advertisement_Canopy_Fee";



	public static List<String> getRolesByTenantId(String tenantId, List<Role> roles) {
		List<String> roleCodes = roles.stream()
				.filter(role -> StringUtils.equalsIgnoreCase(role.getTenantId(), tenantId)).map(role -> role.getCode())
				.collect(Collectors.toList());
		return roleCodes;
	}
	
	public String getStatusOrAction(String action, Boolean fetchValue)
	{
		Map<String, String> map = new HashMap<>();
		map.put(ACTION_INITIATE, STATUS_INITIATED);
		map.put(ACTION_FORWARD_TO_VERIFIER, STATUS_PENDINGFORVERIFICATION);
		map.put(ACTION_RETURN_TO_INITIATOR_FOR_PAYMENT, STATUS_PENDINGFORPAYMENT);
		map.put(ACTION_RETURN_TO_INITIATOR, STATUS_PENDINGFORMODIFICATION);
		map.put(ACTION_FORWARD_TO_APPROVER, STATUS_PENDINGFORAPPROVAL);
		map.put(STATUS_REVOKED, STATUS_REVOKED);
		map.put(STATUS_CANCEL, STATUS_CANCEL);
		map.put(STATUS_APPROVED, STATUS_APPROVED);
		
		
		if (!fetchValue) {
			// return key
			for (Map.Entry<String, String> entry : map.entrySet()) {
				if (entry.getValue().equals(action)) {
					return entry.getKey();
				}
			}
		}
		return map.get(action);
		
	}
}
