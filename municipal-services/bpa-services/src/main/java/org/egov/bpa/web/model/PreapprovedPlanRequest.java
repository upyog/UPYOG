package org.egov.bpa.web.model;

import java.util.Objects;

import javax.validation.Valid;

import org.egov.common.contract.request.RequestInfo;
import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Validated
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PreapprovedPlanRequest {
	@JsonProperty("RequestInfo")
	private RequestInfo requestInfo = null;

	@JsonProperty("preapprovedPlan")
	private PreapprovedPlan preapprovedPlan = null;

	public PreapprovedPlanRequest requestInfo(RequestInfo requestInfo) {
		this.requestInfo = requestInfo;
		return this;
	}

	/**
	 * Get requestInfo
	 * 
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

	public PreapprovedPlanRequest reapprovedPlan(PreapprovedPlan preapprovedPlan) {
		this.preapprovedPlan = preapprovedPlan;
		return this;
	}

	/**
	 * Get BPA
	 * 
	 * @return BPA
	 **/
	@ApiModelProperty(value = "")

	@Valid
	public PreapprovedPlan getPreapprovedPlan() {
		return preapprovedPlan;
	}

	public void setPreapprovedPlan(PreapprovedPlan preapprovedPlan) {
		this.preapprovedPlan = preapprovedPlan;
	}

	@Override
	public boolean equals(java.lang.Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		PreapprovedPlanRequest preapprovedPlanRequest = (PreapprovedPlanRequest) o;
		return Objects.equals(this.requestInfo, preapprovedPlanRequest.requestInfo)
				&& Objects.equals(this.preapprovedPlan, preapprovedPlanRequest.preapprovedPlan);
	}

	@Override
	public int hashCode() {
		return Objects.hash(requestInfo, preapprovedPlan);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("class BPARequest {\n");

		sb.append("    requestInfo: ").append(toIndentedString(requestInfo)).append("\n");
		sb.append("    preapprovedPlan: ").append(toIndentedString(preapprovedPlan)).append("\n");
		sb.append("}");
		return sb.toString();
	}

	/**
	 * Convert the given object to string with each line indented by 4 spaces
	 * (except the first line).
	 */
	private String toIndentedString(java.lang.Object o) {
		if (o == null) {
			return "null";
		}
		return o.toString().replace("\n", "\n    ");
	}
}
