package org.egov.finance.master.model.request;

import java.util.List;

import org.egov.finance.master.entity.CChartOfAccountDetail;
import org.egov.finance.master.model.RequestInfo;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.Valid;

public class CChartOfAccountDetailRequest {

	@JsonProperty("RequestInfo")
	private RequestInfo requestInfo;

	@Valid
	@JsonProperty("accountDetailList")
	private List<CChartOfAccountDetail> accountDetailList;

}
