package org.upyog.cdwm.enums;

@SuppressWarnings("java:S6548") // Status enum; additional values will be added as workflow states evolve
public enum CNDStatus {

	BOOKING_CREATED;

	String status;

	public String getStatus() {
		return status;
	}

}
