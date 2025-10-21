package org.egov.ndc.web.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.validation.Valid;

import org.egov.common.contract.response.ResponseInfo;
import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

/**
 * Contains the ResponseMetadate and the main applications contract
 */
@ApiModel(description = "Contains the ResponseMetadate and the main applications contract")
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2020-07-30T05:43:01.798Z[GMT]")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NdcResponse {
  @JsonProperty("ResponseInfo")
  private ResponseInfo responseInfo = null;

  @JsonProperty("Ndc")
  @Valid
  private List<Ndc> ndc = null;
  
  @JsonProperty("count")
  private Integer count;

  public NdcResponse responseInfo(ResponseInfo responseInfo) {
    this.responseInfo = responseInfo;
    return this;
  }

  /**
   * Get responseInfo
   * @return responseInfo
  **/
  @ApiModelProperty(value = "")
  
    @Valid
    public ResponseInfo getResponseInfo() {
    return responseInfo;
  }

  public void setResponseInfo(ResponseInfo responseInfo) {
    this.responseInfo = responseInfo;
  }

  public NdcResponse ndc(List<Ndc> ndc) {
    this.ndc = ndc;
    return this;
  }

  public NdcResponse addNdcItem(Ndc ndcItem) {
    if (this.ndc == null) {
      this.ndc = new ArrayList<Ndc>();
    }
    this.ndc.add(ndcItem);
    return this;
  }

  /**
   * Get ndc
   * @return ndc
  **/
  @ApiModelProperty(value = "")
      @Valid
    public List<Ndc> getNdc() {
    return ndc;
  }

  public void setNdc(List<Ndc> ndc) {
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
    NdcResponse ndcResponse = (NdcResponse) o;
    return Objects.equals(this.responseInfo, ndcResponse.responseInfo) &&
        Objects.equals(this.ndc, ndcResponse.ndc);
  }

  @Override
  public int hashCode() {
    return Objects.hash(responseInfo, ndc);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class NdcResponse {\n");
    
    sb.append("    responseInfo: ").append(toIndentedString(responseInfo)).append("\n");
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
