package org.egov.pg.service.gateways.nttdata;

public class ResponseDetails {
	private String statusCode;
	private String message;
	private String description;
	public String getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	@Override
	public String toString() {
		return "ResponseDetails [statusCode=" + statusCode + ", message=" + message + ", description=" + description
				+ ", getStatusCode()=" + getStatusCode() + ", getMessage()=" + getMessage() + ", getDescription()="
				+ getDescription() + ", getClass()=" + getClass() + ", hashCode()=" + hashCode() + ", toString()="
				+ super.toString() + "]";
	}
	

}
