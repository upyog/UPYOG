package org.egov.pg.service.gateways.nttdata;


import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class ResponseParser
{
 @JsonProperty("payInstruments")
  private List<PayInstrument> payInstrument;

  public List<PayInstrument> getPayInstrument()
  {
    return this.payInstrument;
  }

  public void setPayInstrument(List<PayInstrument> payInstrument) {
    this.payInstrument = payInstrument;
  }
  
  @JsonProperty("payInstrument")
  private PayInstrument refundPayInstrument;
  
 

  public PayInstrument getRefundPayInstrument() {
	return refundPayInstrument;
}

  public void setRefundPayInstrument(PayInstrument refundPayInstrument) {
	this.refundPayInstrument = refundPayInstrument;
  }

  public String toString()
  {
    return "ClassPojo [payInstrument = " + this.payInstrument + "]";
  }
}
