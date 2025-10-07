package org.egov.ndc.web.model;

import java.util.Objects;

import javax.validation.Valid;

import org.egov.common.contract.request.RequestInfo;
import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * A object to bind the metadata contract and main applications contract
 */
@ApiModel(description = "A object to bind the metadata contract and main applications contract")
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2020-07-30T05:26:25.138Z[GMT]")
public class NdcRequest {
  @JsonProperty("RequestInfo")
  private RequestInfo requestInfo = null;

  @JsonProperty("Ndc")
  private Ndc ndc = null;

  public NdcRequest requestInfo(RequestInfo requestInfo) {
    this.requestInfo = requestInfo;
    return this;
  }

  /**
   * Get requestInfo
   * @return requestInfo
  **/
  @ApiModelProperty(value = "")
  
    @Valid
    public RequestInfo getRequestInfo() {
    return requestInfo;
  }

  public void setRequestInfo(RequestInfo requestInfo) {
    this.requestInfo = requestInfo;
  }

  public NdcRequest ndc(Ndc ndc) {
    this.ndc = ndc;
    return this;
  }

  /**
   * Get ndc
   * @return ndc
  **/
  @ApiModelProperty(value = "")
  
    @Valid
    public Ndc getNdc() {
    return ndc;
  }

  public void setNdc(Ndc ndc) {
    this.ndc = ndc;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    NdcRequest ndcRequest = (NdcRequest) o;
    return Objects.equals(this.requestInfo, ndcRequest.requestInfo) &&
        Objects.equals(this.ndc, ndcRequest.ndc);
  }

  @Override
  public int hashCode() {
    return Objects.hash(requestInfo, ndc);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class NdcRequest {\n");
    
    sb.append("    requestInfo: ").append(toIndentedString(requestInfo)).append("\n");
    sb.append("    ndc: ").append(toIndentedString(ndc)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}
