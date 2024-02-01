package org.egov.pg.service.gateways.nttdata;

public class OtsTransaction {
	
		  private PayInstrument payInstrument;

		  public PayInstrument getPayInstrument()
		  {
		    return this.payInstrument;
		  }

		  public void setPayInstrument(PayInstrument payInstrument) {
		    this.payInstrument = payInstrument;
		  }

		  public String toString()
		  {
		    return "Test [payInstrument=" + this.payInstrument + "]";
		  }
}
