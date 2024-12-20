package org.egov.swcalculation.web.models;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class BillSearch {
	@JsonProperty("id")
	private String  id = null;
	
	@JsonProperty("tenantId")
	private String tenantId = null;
	
	@JsonProperty("demandid")
	private String demandid = null;
	
	
}