package org.egov.echallan.enums;

public enum ChallanStatusEnum {
	AVAILABLE,
	CHALLAN_CREATED,
	CHALLAN_GENERATED,
	CANCELLATION_REQUESTED,
	PENDING_FOR_PAYMENT,
	PENDING_FOR_SETTLEMENT,  // Added to match workflow state
	PENDING_FOR_VERIFICATION,
	PAYMENT_FAILED,
	CANCELLED,
	BOOKING_EXPIRED;
	String status;
	
	public String getStatus() {
		return status;
	}

}
