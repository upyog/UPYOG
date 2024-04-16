package org.egov.notice.web.model;

import java.util.List;
import java.util.Map;

import org.egov.notice.web.model.workflow.ProcessInstance;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.egov.notice.enums.NoticeType;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Notice {

	@JsonProperty("noticeType")
	private  NoticeType noticeType ;
	@JsonProperty("noticeuuid")
	private String noticeuuid;
	@JsonProperty("noticeNumber")
	private String noticeNumber;
	@JsonProperty("noticeComment")
	private List<NoticeComment>noticeComment;
	private String name;
	private String address;
	private String propertyId;
	private String assessmentYear;
	private String acknowledgementNumber;
	private String dateOfAnnualRet;
	private String entryDate;
	private String entryTime;
	private String place;
}
