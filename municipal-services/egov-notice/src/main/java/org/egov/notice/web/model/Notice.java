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
	
	@JsonProperty("tenantId")
	private  String tenantId ;
	
	@JsonProperty("noticeuuid")
	private String noticeuuid;
	
	@JsonProperty("noticeNumber")
	private String noticeNumber;
	
	@JsonProperty("noticeComment")
	private List<NoticeComment>noticeComment;
	
	@JsonProperty("name")
	private String name;
	
	@JsonProperty("address")
	private String address;
	
	@JsonProperty("propertyId")
	private String propertyId;
	
	@JsonProperty("assessmentYear")
	private String assessmentYear;
	
	@JsonProperty("acknowledgementNumber")
	private String acknowledgementNumber;
	
	@JsonProperty("dateOfAnnualRet")
	private String dateOfAnnualRet;
	
	@JsonProperty("entryDate")
	private String entryDate;
	
	@JsonProperty("entryTime")
	private String entryTime;
	
	@JsonProperty("place")
	private String place;
	
	@JsonProperty("AuditDetails")
	private AuditDetails auditDetails;
}
