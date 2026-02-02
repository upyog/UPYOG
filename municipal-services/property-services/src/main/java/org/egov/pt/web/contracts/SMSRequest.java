package org.egov.pt.web.contracts;

import lombok.*;


@Getter
//@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class SMSRequest {
    private String mobileNumber;
    private String message;
    private String templateId;
    
    public SMSRequest(String mobileNumber, String message,String templateId){
    	this.message= message;
    	this.mobileNumber=mobileNumber;
    	this.templateId=templateId;
    	
    }
    
    public SMSRequest(String mobileNumber, String message){
    	this.message= message;
    	this.mobileNumber=mobileNumber;
  
    	
    }
    
}
