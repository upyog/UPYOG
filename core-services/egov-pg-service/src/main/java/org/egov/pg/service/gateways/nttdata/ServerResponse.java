package org.egov.pg.service.gateways.nttdata;

public class ServerResponse {
	 private String atomTokenId;
	  private ResponseDetails responseDetails;

	  public String getAtomTokenId()
	  {
	    return this.atomTokenId;
	  }

	  public void setAtomTokenId(String atomTokenId)
	  {
	    this.atomTokenId = atomTokenId;
	  }

	  public ResponseDetails getResponseDetails()
	  {
	    return this.responseDetails;
	  }

	  public void setResponseDetails(ResponseDetails responseDetails)
	  {
	    this.responseDetails = responseDetails;
	  }

	  public String toString()
	  {
	    return "ClassPojo [atomTokenId = " + this.atomTokenId + ", responseDetails = " + this.responseDetails + "]";
	  }
}
