package org.egov.finance.report.model.request;

import java.util.List;

import org.egov.finance.report.entity.CChartOfAccountDetail;
import org.egov.finance.report.model.RequestInfo;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.Valid;

public class CChartOfAccountDetailRequest {

	@JsonProperty("RequestInfo")
	private RequestInfo requestInfo;

	@Valid
	@JsonProperty("accountDetailList")
	private List<CChartOfAccountDetail> accountDetailList;

}
