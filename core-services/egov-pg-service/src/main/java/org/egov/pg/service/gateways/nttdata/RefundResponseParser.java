package org.egov.pg.service.gateways.nttdata;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class RefundResponseParser {

	  @JsonProperty("payInstrument")
	  private PayInstrument refundPayInstrument;
	  
	 

	  public PayInstrument getRefundPayInstrument() {
		return refundPayInstrument;
	}

	  public void setRefundPayInstrument(PayInstrument refundPayInstrument) {
		this.refundPayInstrument = refundPayInstrument;
	  }
}
