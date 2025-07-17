/**
 * @author bpattanayak
 * @date 2 Jul 2025
 */

package org.egov.finance.report.model.request;

import org.egov.finance.report.model.RequestInfo;
import org.egov.finance.report.model.Statement;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class BalanceSheetReportRequest {
	
	private RequestInfo requestInfo;
	@JsonProperty("BalanceSheet")
	private Statement statement;

}
