package org.egov.pg.service.gateways.nttdata;

public class SettlementDetails {
	private String reconStatus;
	
	public String getReconStatus() {
		return reconStatus;
	}

	public void setReconStatus(String reconStatus) {
		this.reconStatus = reconStatus;
	}

	@Override
	public String toString() {
		return "SettlementDetails [reconStatus=" + reconStatus + ", getReconStatus()=" + getReconStatus()
		+ ", getClass()=" + getClass() + ", hashCode()=" + hashCode() + ", toString()=" + super.toString()
		+ "]";
	}

}
