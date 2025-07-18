package org.egov.bpa.web.model;

import java.util.List;
import java.util.Objects;

import javax.validation.Valid;

import org.egov.common.contract.response.ResponseInfo;
import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Contains the ResponseHeader and the Preapproved Plan
 */
@Validated
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class PreapprovedPlanResponse {
	@JsonProperty("ResponseInfo")
	private ResponseInfo responseInfo;

	@JsonProperty("preapprovedPlan")
	private List<PreapprovedPlan> preapprovedPlan;

	public PreapprovedPlanResponse responseInfo(ResponseInfo responseInfo) {
		this.responseInfo = responseInfo;
		return this;
	}

	/**
	 * Get responseInfo
	 * 
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

	public PreapprovedPlanResponse preapprovedPlanResponse(List<PreapprovedPlan> preapprovedPlan) {
		this.preapprovedPlan = preapprovedPlan;
		return this;
	}

	@Override
	public boolean equals(java.lang.Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		PreapprovedPlanResponse preapprovedPlanResponse = (PreapprovedPlanResponse) o;
		return Objects.equals(this.responseInfo, preapprovedPlanResponse.responseInfo)
				&& Objects.equals(this.preapprovedPlan, preapprovedPlanResponse.preapprovedPlan);
	}

	@Override
	public int hashCode() {
		return Objects.hash(responseInfo, preapprovedPlan);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("class PreapprovedPlanResponse {\n");

		sb.append("    responseInfo: ").append(toIndentedString(responseInfo)).append("\n");
		sb.append("    PreapprovedPlan: ").append(toIndentedString(preapprovedPlan)).append("\n");
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
