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

  public String toString()
  {
    return "ClassPojo [payInstrument = " + this.payInstrument + "]";
  }
}
