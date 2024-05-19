package org.egov.wscalculation.web.models;

import java.util.Set;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class BillGeneratorReq  {
	
		@JsonProperty("requestInfoWrapper")
		@NotNull
        private RequestInfoWrapper requestInfoWrapper;
		
		private long taxPeriodFrom;
		
		
		private long taxPeriodTo;
		
		private Set<String> consumerCodes;

        private BillScheduler billSchedular;
        
        private String tenantId;

}

