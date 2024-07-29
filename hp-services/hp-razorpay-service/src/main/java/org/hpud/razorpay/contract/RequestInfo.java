package org.hpud.razorpay.contract;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestInfo {
	
	 private String apiId;

	    private String ver;

	    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss", timezone = "IST")
	    private Date ts;

	    private String action;

	    private String did;

	    private String key;

	    private String msgId;

	    private String authToken;
	    
	    private String requesterId;

	    private String correlationId;

	    private UserInfo userInfo;

}