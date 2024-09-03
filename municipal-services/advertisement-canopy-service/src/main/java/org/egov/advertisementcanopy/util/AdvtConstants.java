package org.egov.advertisementcanopy.util;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@Data
public class AdvtConstants {

	@Value("${workflow.context.path}")
	public String workflowHost;

	@Value("${workflow.transition.path}")
	public String workflowEndpointTransition;

	@Value("${workflow.business.search}")
	public String workflowBusinessServiceSearchPath;

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

	public static final String ADVERTISEMENT_HOARDING = "Advertising Hoarding";
	public static final String CANOPY = "Canopy";

	public static final String SITE_BOOKING_CREATE_KAFKA_TOPIC = "create-site-booking";

	public static final String SITE_BOOKING_UPDATE_KAFKA_TOPIC = "update-site-booking";

	public static final String ACTION_INITIATE = "INITIATE";

	public static final String ACTION_FORWARD_TO_VERIFIER = "FORWARD_TO_VERIFIER";

	public static final String ACTION_FORWARD_TO_APPROVER = "FORWARD_TO_APPROVER";

	public static final String ACTION_RETURN_TO_INITIATOR = "RETURN_TO_INITIATOR";

	public static final String ACTION_RETURN_TO_VERIFIER = "RETURN_TO_VERIFIER";

	public static final String ACTION_RETURN_TO_INITIATOR_FOR_PAYMENT = "RETURN_TO_INITIATOR_FOR_PAYMENT";

	public static final String STATUS_INITIATED = "INITIATED";

	public static final String STATUS_PENDINGFORMODIFICATION = "PENDINGFORMODIFICATION";

	public static final String STATUS_PENDINGFORVERIFICATION = "PENDINGFORVERIFICATION";

	public static final String STATUS_PENDINGFORPAYMENT = "PENDINGFORPAYMENT";

	public static final String STATUS_PENDINGFORAPPROVAL = "PENDINGFORAPPROVAL";

	public static final String STATUS_APPROVED = "APPROVED";

	public static final String BUSINESS_SERVICE = "ADVT";

    public static final String BILLING_TAX_HEAD_MASTER_CODE = "LCF.Advertisement_Canopy_Fee";

	public String getStatusOrAction(String action, Boolean fetchValue) {

		Map<String, String> map = new HashMap<>();

		map.put(ACTION_INITIATE, STATUS_INITIATED);
		map.put(ACTION_FORWARD_TO_VERIFIER, STATUS_PENDINGFORVERIFICATION);
		map.put(ACTION_RETURN_TO_INITIATOR_FOR_PAYMENT, STATUS_PENDINGFORPAYMENT);
		map.put(ACTION_RETURN_TO_INITIATOR, STATUS_PENDINGFORMODIFICATION);
		map.put(ACTION_FORWARD_TO_APPROVER, STATUS_PENDINGFORAPPROVAL);
		map.put(STATUS_APPROVED, STATUS_APPROVED);

		if (!fetchValue) {
			// return key
			for (Map.Entry<String, String> entry : map.entrySet()) {
				if (entry.getValue().equals(action)) {
					return entry.getKey();
				}
			}
		}
		// return value
		return map.get(action);
	}
}
