package org.upyog.sv.enums;

public enum StreetVendingApplicationStatus {
	APPLIED,
	CITIZENACTIONREQUIRED,
	INPECTIONPENDING,
	APPROVALPENDING,
	APPROVED,
	REJECTED,
	REGISTRATIONCOMPLETED;
	
	String status;
	
	public String getStatus() {
		return status;
	}

}
