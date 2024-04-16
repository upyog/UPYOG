package org.egov.notice.web.model;

import java.util.List;

import javax.json.bind.annotation.JsonbProperty;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.response.ResponseInfo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NoticeResponse {
	@JsonbProperty("responseInfo")
	private ResponseInfo responseInfo;
	
	@JsonbProperty("notice")
	List<Notice> notice;

}
