package org.upyog.dx.upyogdata.pfms.enums;

/**
 * Represents the processing status of a PFMS transaction.
 */
@SuppressWarnings("java:S6548")
public enum Status {

	INITIATED,
	PROCESSING,
	SUCCESS,
	FAILED;

	public String getStatus() {
		return name();
	}
}
