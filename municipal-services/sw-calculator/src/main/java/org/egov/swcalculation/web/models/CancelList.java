package org.egov.swcalculation.web.models;
import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.NotNull;
import org.apache.coyote.RequestInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class CancelList {
	
	
	
	
	@JsonProperty("consumerCode")
	private String consumerCode;
	@JsonProperty("businessService")
	private String businessService;
	public String getBusinessService() {
		
		return businessService;
	}
	public String getConsumerCode() {
		
		return consumerCode;
	}
	
	
	@JsonProperty("tenantId")
	private String tenantId;

	@JsonProperty("demandid")
	private String demandid;

	public String gettenantId() {
		
		return tenantId;
	}

	public String getdemandid() {
		
		return demandid;
	}
	
}