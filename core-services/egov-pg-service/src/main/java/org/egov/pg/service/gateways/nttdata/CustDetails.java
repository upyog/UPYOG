package org.egov.pg.service.gateways.nttdata;



public class CustDetails {
	
		private String custFirstName;
		private String custEmail;
		private String custMobile;
		private BillingInfo billingInfo;
		public String getCustFirstName() {
			return custFirstName;
		}
		public void setCustFirstName(String custFirstName) {
			this.custFirstName = custFirstName;
		}
		public String getCustEmail() {
			return custEmail;
		}
		public void setCustEmail(String custEmail) {
			this.custEmail = custEmail;
		}
		public String getCustMobile() {
			return custMobile;
		}
		public void setCustMobile(String custMobile) {
			this.custMobile = custMobile;
		}
		public BillingInfo getBillingInfo() {
			return billingInfo;
		}
		public void setBillingInfo(BillingInfo billingInfo) {
			this.billingInfo = billingInfo;
		}
		@Override
		public String toString() {
			return "CustDetails [custFirstName=" + custFirstName + ", custEmail=" + custEmail + ", custMobile="
					+ custMobile + ", billingInfo=" + billingInfo + ", getCustFirstName()=" + getCustFirstName()
					+ ", getCustEmail()=" + getCustEmail() + ", getCustMobile()=" + getCustMobile()
					+ ", getBillingInfo()=" + getBillingInfo() + ", getClass()=" + getClass() + ", hashCode()="
					+ hashCode() + ", toString()=" + super.toString() + "]";
		}
	
	
}
