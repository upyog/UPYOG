package org.egov.pt.models;

import java.util.List;


import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.egov.pt.models.enums.NoticeType;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Notice {



	@JsonProperty("noticeType")
	private NoticeType noticeType;
	
	@JsonProperty("tenantId")
	private  String tenantId ;
	
	@JsonProperty("noticeuuid")
	private String noticeuuid;
	
	@JsonProperty("noticeNumber")
	private String noticeNumber;
	
	@JsonProperty("NoticeComment")
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
		
	@JsonProperty("resolutionOn")
	private String resolutionOn;
	
	@JsonProperty("dated")
	private String dated;
	
	@JsonProperty("designation")
	private String designation;
	
	@JsonProperty("authorisedpersonName")
	private String authorisedpersonName;
	
	@JsonProperty("mobilenumber")
	private String mobilenumber;
	
	@JsonProperty("penaltyAmount")
	private String penaltyAmount;
	
	@JsonProperty("appealNo")
	private String appealNo;
	
	@JsonProperty("Rule_23")
	private Boolean rule_23;
	
	@JsonProperty("Rule_33")
	private Boolean rule_33;
	
	@JsonProperty("Rule_34")
	private Boolean rule_34;
	
	@JsonProperty("Rule_36")
	private Boolean rule_36;
	
	@JsonProperty("FailedtoProduceNecessaryDocuments")
	private Boolean failedtoproducenecessarydocuments;
	
	@JsonProperty("WillfullyfurnishesIncorrectInformation")
	private Boolean willfullyfurnishesincorrectinformation;
	
	@JsonProperty("ObstructAnyAuthorityAppointed")
	private Boolean obstructanyauthorityappointed;
	
	@JsonProperty("auditDetails")
	private AuditDetails auditDetails;

}
