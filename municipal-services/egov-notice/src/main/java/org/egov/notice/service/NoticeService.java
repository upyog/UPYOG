package org.egov.notice.service;

import org.egov.notice.enums.NoticeType;
import org.egov.notice.web.model.Notice;
import org.egov.notice.web.model.NoticeResponse;
import org.egov.tracer.model.CustomException;

import com.cedarsoftware.util.StringUtilities;

import org.egov.common.contract.request.RequestInfo;

public class NoticeService {
	
	public NoticeResponse saveNoticeData(Notice noticeFromRequest,RequestInfo requestInfo)
	{
		NoticeResponse response = null;
		if(noticeFromRequest.getNoticeType().equals(NoticeType.REC_MIS_DEF_RET))
		{
			validateForRecMistakeDefectiveReturn(noticeFromRequest);
			
			
		}
		return response;
	}

	private void validateForRecMistakeDefectiveReturn(Notice noticeFromRequest) {
		if(StringUtilities.isEmpty(noticeFromRequest.getAcknowledgementNumber())) {
			throw new CustomException("INVALID_ACK_NUM", "Invalid Acknowledgement Number");
			
		}
		if(StringUtilities.isEmpty(noticeFromRequest.getAssessmentYear())) {
			throw new CustomException("INVALID_ASMT_YEAR", "Invalid Assessment Year");
			
		}	
		
		if(StringUtilities.isEmpty(noticeFromRequest.getPropertyId())) {
			throw new CustomException("INVALID_PROP_NUM", "Invalid Property Id or UPIN");
			
		}
		
		
		
		
	}

}
