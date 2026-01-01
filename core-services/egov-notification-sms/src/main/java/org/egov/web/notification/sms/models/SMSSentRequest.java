package org.egov.web.notification.sms.models;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.egov.common.contract.request.RequestInfo;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SMSSentRequest {

	@JsonProperty("RequestInfo")
	private RequestInfo requestInfo;

	@Pattern(regexp = "^[0-9]{10}$", message = "MobileNumber should be 10 digit number")
	private String mobileNumber;

	@Size(max = 1000)
	private String message;
	private Category category;
	private Long expiryTime;

	// Unused for future upgrades
	private String locale;
	private String tenantId;
	private String email;
	private String[] users;
	private String templateId;

	private String templateName;
	
	private String billId;
	
	public String getBillId() {
	        return billId;
	 }

	 public void setBillId(String billId) {
	        this.billId = billId;
	 }
	    

	public boolean isValid() {
		return isNotEmpty(mobileNumber) && isNotEmpty(message) && isNotEmpty(templateName);
	}

}